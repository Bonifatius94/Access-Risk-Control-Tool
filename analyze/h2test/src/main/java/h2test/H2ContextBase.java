package h2test;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.Closeable;
import java.util.List;

public abstract class H2ContextBase implements Closeable {

    // +++++++++++++++++++++++++++++++
    // ++        Constructors       ++
    // +++++++++++++++++++++++++++++++

    public H2ContextBase(String username, String password) {

        sessionFactory = initSessionFactory(username, password);
    }

    // +++++++++++++++++++++++++++++++
    // ++     Session Initiation    ++
    // +++++++++++++++++++++++++++++++

    private SessionFactory sessionFactory;

    private SessionFactory initSessionFactory(String username, String password) throws HibernateException {

        // init config with settings from hibernate.properties file
        Configuration config = new Configuration();

        // set username and password
        config.setProperty("hibernate.connection.username", username);
        config.setProperty("hibernate.connection.password", password);

        // init hibernate DAO classes
        getAnnotatedClasses().forEach(config::addAnnotatedClass);

        return config.buildSessionFactory();
    }

    protected abstract List<Class> getAnnotatedClasses();

    public void changeUser(String username, String password) {

        // re-init session factory with new username and password
        initSessionFactory(username, password);
    }

    // +++++++++++++++++++++++++++++++
    // ++ CRUD base implementations ++
    // +++++++++++++++++++++++++++++++

    @SuppressWarnings({"unchecked"})
    public <T extends IRecord>  List<T> queryDataset(String sqlQuery) {

        List<T> records;

        try (Session session = sessionFactory.openSession()) {

            records = session.createQuery(sqlQuery).list();
        }

        return records;
    }

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
