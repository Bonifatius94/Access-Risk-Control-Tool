package junit.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class H2AccountsTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {

        System.out.print("cleaning up datebase ...");
        deleteFileIfExists(getDefaultDatabaseFilePath());
        System.out.println(" done");

        String currentExeFolder = System.getProperty("user.dir");
        Path scriptPath = Paths.get(currentExeFolder, "target", "test-classes", "scripts", "create_test_data_seed.sql");
        List<String> sqlStatements = Files.readAllLines(scriptPath);

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            try (Session session = context.openSession()) {

                Transaction transaction = session.beginTransaction();
                sqlStatements.forEach(sql -> session.createNativeQuery(sql).executeUpdate());
                transaction.commit();
            }
        }

        System.out.println("created a new test database and inserted test data");
    }

    private static String getDefaultDatabaseFilePath() {

        String currentExeFolder = System.getProperty("user.dir");
        return Paths.get(currentExeFolder, "foo.h2.mv.db").toAbsolutePath().toString();
    }

    private static void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

    @Test
    public void testCreateDatabaseUser() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new database accounts
            context.createDatabaseUser("FooAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("FooDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("FooViewer", "foobar", DbUserRole.Viewer);

            // query accounts
            List<DbUser> users = context.getDatabaseUsers();
            ret = users.size() == 6;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testChangeUserRole() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            context.createDatabaseUser("Foo", "foobar", DbUserRole.Viewer);

            // check if an additional user was created
            List<DbUser> users = context.getDatabaseUsers();
            boolean roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals("FOO")).findFirst().get().getRole() == DbUserRole.Viewer;
            assert(roleOk);

            // change role to data analyst
            context.changeUserRole("Foo", DbUserRole.DataAnalyst);

            // check if the role was changed
            users = context.getDatabaseUsers();
            roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals("FOO")).findFirst().get().getRole() == DbUserRole.DataAnalyst;

            ret = roleOk;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testChangePassword() {

        // TODO: implement test
    }

    @Test
    public void testDeleteDatabaseUser() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            context.createDatabaseUser("Foo", "foobar", DbUserRole.Viewer);

            // query new user
            DbUser user = context.getDatabaseUsers().stream().filter(x -> x.getUsername().equals("FOO")).findFirst().get();

            // test delete role
            context.deleteDatabaseUser("Foo");

            // check if user was deleted
            user = context.getDatabaseUsers().stream().filter(x -> x.getUsername().equals("FOO")).findFirst().orElse(null);

            ret = user == null;

        } catch (Exception ex) {

            System.out.println("db account test failed");
            ex.printStackTrace();
        }

        assert(ret);
    }

}
