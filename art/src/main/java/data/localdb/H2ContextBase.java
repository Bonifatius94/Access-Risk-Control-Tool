package data.localdb;

import java.io.Closeable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import tools.tracing.TraceOut;

public abstract class H2ContextBase implements Closeable {

    // +++++++++++++++++++++++++++++++
    // ++        Constructors       ++
    // +++++++++++++++++++++++++++++++

    /**
     * This constructor initializes a new H2 database context to the given database file using to given login credentials.
     *
     * @param filePath the H2 database file path (file system automatically appends '.mv.db')
     * @param username the username for login
     * @param password the password for login
     */
    public H2ContextBase(String filePath, String username, String password) throws Exception {

        this.filePath = filePath;
        this.username = username;
        this.password = password;

        boolean isNewDatabase = !new File(getFilePath()).exists();
        sessionFactory = initSessionFactory(filePath, username, password);

        if (isNewDatabase) {
            createNewDatabase();
        }
    }

    // +++++++++++++++++++++++++++++++
    // ++          Members          ++
    // +++++++++++++++++++++++++++++++

    private String filePath;
    private SessionFactory sessionFactory;

    private String username;
    private String password;

    // +++++++++++++++++++++++++++++++
    // ++     Session Initiation    ++
    // +++++++++++++++++++++++++++++++

    /**
     * This method initializes a new session factory with settings from hibernate.properties file and the overloaded login credentials.
     *
     * @param username the username used for database authentication
     * @param password the password used for database authentication
     * @return a new instance of session factory
     * @throws HibernateException caused by database error
     */
    private SessionFactory initSessionFactory(String filePath, String username, String password) throws HibernateException {

        // configure hibernate logging level
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

        // init config with settings from hibernate.properties file
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

        builder.applySetting(Environment.DRIVER, "org.h2.Driver");
        builder.applySetting(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
        builder.applySetting(Environment.URL, "jdbc:h2:file:" + filePath);
        builder.applySetting(Environment.USER, username);
        builder.applySetting(Environment.PASS, password);
        //builder.applySetting(Environment.SHOW_SQL, "true");

        // apply annotated data entity classes
        final StandardServiceRegistry registry = builder.build();
        final MetadataSources metadataSources = new MetadataSources(registry);
        getAnnotatedClasses().forEach(metadataSources::addAnnotatedClass);

        Metadata metadata = metadataSources.getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

        // build the session factory and return it
        return sessionFactory;
    }

    /**
     * This method create a new database file with the desired database schema.
     */
    protected abstract void createNewDatabase() throws Exception;

    /**
     * This method gets the data entity classes of the context that are applied to the session factory.
     *
     * @return a list of classes that match a database table (marked by @Entity annotation)
     */
    protected abstract List<Class> getAnnotatedClasses();

    protected Session openSession() {
        return sessionFactory.openSession();
    }

    /**
     * Apply new user credentials to the session factory to create connections for the new user.
     *
     * @param username new username of the sessions created by the session factory
     * @param password new password of the sessions created by the session factory
     */
    @Deprecated
    protected void changeUser(String username, String password) {

        // close old session factory
        sessionFactory.close();

        // create new session factory with given login credentials
        sessionFactory = initSessionFactory(filePath, username, password);
    }

    // +++++++++++++++++++++++++++++++
    // ++ CRUD base implementations ++
    // +++++++++++++++++++++++++++++++

    /**
     * This method opens a new database session, executes a sql query with given sql and returns the results of the query as java.util.List.
     *
     * @param sql the sql query text that is executed by the query
     * @param <T> the inner type of the result set (implicitly set)
     * @return a list of DAO entity objects from the result set of the query (on error the list = null)
     */
    @SuppressWarnings({"unchecked"})
    public <T> List<T> queryDataset(String sql) {

        List<T> records;

        try (Session session = sessionFactory.openSession()) {

            records = session.createQuery(sql).list();
        }

        return records;
    }

    /**
     * This method inserts a DAO into database. Therefore a new session and transaction is created. On error the changes are rolled back.
     *
     * @param record the DAO to be inserted into database
     * @param <T> the type of the DAO (implicitly set)
     */
    public <T> void insertRecord(T record) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            try {

                transaction = session.beginTransaction();
                session.save(record);
                session.flush();
                transaction.commit();

            } catch (Exception ex) {

                if (transaction != null) {
                    transaction.rollback();
                }

                throw ex;
            }
        }
    }

    /**
     * This method updates an existing DAO in database. Therefore a new session and transaction is created. On error the changes are rolled back.
     *
     * @param record the DAO to be updated
     * @param <T> the type of the DAO (implicitly set)
     */
    public <T> void updateRecord(T record) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            try {

                transaction = session.beginTransaction();
                session.update(record);
                session.flush();
                transaction.commit();

            } catch (Exception ex) {

                if (transaction != null) {
                    transaction.rollback();
                }

                throw ex;
            }
        }
    }

    /**
     * This method deletes an existing DAO in database. Therefore a new session and transaction is created. On error the changes are rolled back.
     *
     * @param record the DAO to be deleted
     * @param <T> the type of the DAO (implicitly set)
     */
    public <T> void deleteRecord(T record)  {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            try {

                transaction = session.beginTransaction();
                session.delete(record);
                session.flush();
                transaction.commit();

            } catch (Exception ex) {

                if (transaction != null) {
                    transaction.rollback();
                }

                throw ex;
            }
        }
    }

    // +++++++++++++++++++++++++++++++
    // ++      execute script       ++
    // +++++++++++++++++++++++++++++++

    /**
     * This method executes a sql script. Therefore it gets all statement from the script and executes them in one transaction.
     *
     * @param filePath the file path of the script to be executed
     * @throws Exception caused by file errors while reading the script or while executing the sql commands
     */
    public void executeScript(String filePath) throws Exception {

        TraceOut.enter();

        String path = new File(filePath).getAbsolutePath();
        TraceOut.writeInfo("Script Path: '" + path + "'");
        List<String> sqlCommands = getCommands(path);

        try (Session session = sessionFactory.openSession()) {

            Transaction transaction = null;

            try {

                transaction = session.beginTransaction();
                sqlCommands.forEach(sql -> session.createNativeQuery(sql).executeUpdate());
                transaction.commit();

            } catch (Exception ex) {

                if (transaction != null) {
                    transaction.rollback();
                }

                throw ex;
            }
        }

        TraceOut.leave();
    }

    private List<String> getCommands(String filePath) throws Exception {

        StringBuilder builder = new StringBuilder();

        // get all line from script file
        Files.readAllLines(Paths.get(filePath)).stream()
            // remove rest of line after a line comment
            .map(x -> x.contains("--") ? x.substring(0, x.indexOf("--")) : x)
            // append everything to string builder
            .forEach(x -> builder.append(x).append("\n"));

        List<String> commands = new ArrayList<>();
        int quotesCount = 0;
        int start = 0;

        for (int i = 0; i < builder.length(); i++) {

            char c = builder.charAt(i);

            // increase quotes count when a single quote occurs
            if (c == '\'') {

                quotesCount++;

            // check if the end of a command is reached (when quotes count is not even, the semicolon is part of a quote text)
            } else if (c == ';' && quotesCount % 2 == 0) {

                // get the command, apply it to the commands list and reset all temporary variables
                String command = builder.substring(start, i + 1);
                commands.add(command);
                quotesCount = 0;
                start = i + 1;
            }
        }

        return commands;
    }

    // +++++++++++++++++++++++++++++++
    // ++    disposing connection   ++
    // +++++++++++++++++++++++++++++++

    /**
     * This method implements the Closable interface. It disposes the session factory instance.
     */
    @Override
    public void close() {
        sessionFactory.close();
    }

    // +++++++++++++++++++++++++++++++
    // ++     Getters / Setters     ++
    // +++++++++++++++++++++++++++++++

    public String getFilePath() {
        return filePath + ".mv.db";
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
