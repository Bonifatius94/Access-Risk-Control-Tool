package junit.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class H2AccountsTest {

    @BeforeEach
    public void cleanupDatabase() {

        // delete database to ensure a clean test environment
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");
    }

    private static void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

    @Test
    public void testCreateDatabaseUser() {

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new database accounts
            context.createDatabaseUser("TestAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("TestDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("TestViewer", "foobar", DbUserRole.Viewer);

            // query accounts
            List<DbUser> users = context.getDatabaseUsers();
            assert(users.size() == 3);

        } catch (Exception ex) {
            assert(false);
        }
    }

    @Test
    public void testChangeUserRole() {

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new database accounts
            context.createDatabaseUser("TestAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("TestDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("TestViewer", "foobar", DbUserRole.Viewer);

            // query accounts
            List<DbUser> users = context.getDatabaseUsers();
            assert(users.size() == 3);

        } catch (Exception ex) {
            assert(false);
        }
    }

    @Test
    public void testDbAccountManagement() {

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new database accounts
            context.createDatabaseUser("TestAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("TestDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("TestViewer", "foobar", DbUserRole.Viewer);

            // test db accounts query
            List<DbUser> users = context.getDatabaseUsers();
            System.out.println("Db account creation test " + (users.size() == 3 ? "successful" : "failed"));
            System.out.println();
            users.forEach(System.out::println);
            System.out.println();

            // test change role
            context.createDatabaseUser("Foo", "foobar", DbUserRole.Viewer);

            // check if an additional user was created
            users = context.getDatabaseUsers();
            boolean roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals("FOO")).findFirst().orElse(null).getRole() == DbUserRole.Viewer;
            System.out.println("creation of foo account " + ((users.size() == 4 && roleOk) ? "successful" : "failed"));
            System.out.println();
            users.forEach(System.out::println);
            System.out.println();

            context.changeUserRole("Foo", DbUserRole.DataAnalyst);

            // check if the role was changed
            users = context.getDatabaseUsers();
            roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals("FOO")).findFirst().orElse(null).getRole() == DbUserRole.DataAnalyst;
            System.out.println("Db account role change test " + ((users.size() == 4 && roleOk) ? "successful" : "failed"));
            System.out.println();
            users.forEach(System.out::println);
            System.out.println();

            // test change password
            // TODO: implement test

            // test delete role
            context.deleteDatabaseUser("Foo");

            // check if user was deleted
            users = context.getDatabaseUsers();
            System.out.println("Db account deletion test " + (users.size() == 3 ? "successful" : "failed"));
            System.out.println();
            users.forEach(System.out::println);
            System.out.println();

        } catch (Exception ex) {

            System.out.println("db account test failed");
            ex.printStackTrace();
        }
    }

}
