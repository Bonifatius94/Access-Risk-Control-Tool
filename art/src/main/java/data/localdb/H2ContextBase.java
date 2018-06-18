package data.localdb;

import java.io.Closeable;

import java.io.File;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

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
    public H2ContextBase(String filePath, String username, String password) {

        this.filePath = filePath;
        this.username = username;
        this.password = password;
        sessionFactory = initSessionFactory(filePath, username, password);
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

        // init config with settings from hibernate.properties file
        Configuration config = new Configuration();

        // set username and password
        config.setProperty("hibernate.connection.username", username);
        config.setProperty("hibernate.connection.password", password);
        config.setProperty("hibernate.connection.url", "jdbc:h2:file:" + filePath);

        setAdditionalProperties(config);

        // init hibernate data entity classes
        getAnnotatedClasses().forEach(config::addAnnotatedClass);

        return config.buildSessionFactory();
    }

    protected void setAdditionalProperties(Configuration config) {

        // make hibernate auto-create the database schema on first use
        config.setProperty("hibernate.hbm2ddl.auto", new File(filePath + ".mv.db").exists() ? "update" : "create");
    }

    /**
     * This method gets the data entity classes of the context that are applied to the session factory.
     *
     * @return a list of classes that match a database table (marked by @Entity annotation)
     */
    protected abstract List<Class> getAnnotatedClasses();

    public Session openSession() {
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

            transaction = session.beginTransaction();
            session.persist(record);
            //session.flush();
            transaction.commit();

        } catch (Exception ex) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw ex;
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

    /**
     * This method deletes an existing DAO in database. Therefore a new session and transaction is created. On error the changes are rolled back.
     *
     * @param record the DAO to be deleted
     * @param <T> the type of the DAO (implicitly set)
     */
    public <T> void deleteRecord(T record)  {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

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
