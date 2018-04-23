package sqlite;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.Closeable;
import java.util.List;
import java.util.Properties;

// TODO: implement multiple sessions at once
// this article should do the job: https://stackoverflow.com/questions/29364837/hibernate-sqlite-sqlite-busy

public abstract class SqliteContextBase implements Closeable {

    // +++++++++++++++++++++++++++++++
    // ++        Constructors       ++
    // +++++++++++++++++++++++++++++++

    @SuppressWarnings({"WeakerAccess", "unused"})
    public SqliteContextBase(String filePath) {

        sessionFactory = initSessionFactory(filePath);
        //loadSqliteDb(filePath);
    }

    // +++++++++++++++++++++++++++++++
    // ++     Session Initiation    ++
    // +++++++++++++++++++++++++++++++

    @SuppressWarnings("WeakerAccess")
    protected SessionFactory sessionFactory;

    @SuppressWarnings("UnnecessaryLocalVariable")
    private SessionFactory initSessionFactory(String filePath) throws HibernateException {

        Properties prop = new Properties();
        prop.setProperty("hibernate.connection.url", "jdbc:sqlite:" + filePath);
        prop.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");

        Configuration config = new Configuration()
            .addPackage("art.javafx.test.sqlite")
            .addProperties(prop);

        getAnnotatedClasses().forEach(config::addAnnotatedClass);

        return config.buildSessionFactory();
    }

    protected abstract List<Class> getAnnotatedClasses();

    // +++++++++++++++++++++++++++++++
    // ++ CRUD base implementations ++
    // +++++++++++++++++++++++++++++++

    @SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
    public <T extends IRecord>  List<T> queryDataset(String sqlQuery) {

        List<T> records;

        try (Session session = sessionFactory.openSession()) {

            records = session.createQuery(sqlQuery).list();
        }

        return records;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public <T extends IRecord> void insertRecord(T record) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();
            session.save(record);
            session.flush();
            transaction.commit();
        }
        catch (Exception ex) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw ex;
        }
    }

    @SuppressWarnings("unused")
    public <T extends IRecord> void updateRecord(T record) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();
            session.update(record);
            session.flush();
            transaction.commit();
        }
        catch (Exception ex) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw ex;
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public <T extends IRecord> void deleteRecord(T record) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();
            session.delete(record);
            session.flush();
            transaction.commit();
        }
        catch (Exception ex) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw ex;
        }
    }

    // +++++++++++++++++++++++++++++++
    // ++    disposing connection   ++
    // +++++++++++++++++++++++++++++++

    @Override
    public void close() {
        sessionFactory.close();
    }
    
}
