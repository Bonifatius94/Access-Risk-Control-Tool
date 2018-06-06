package data.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;

import java.io.File;
import java.util.List;

public class H2Test {

    /**
     * This method runs a test program.
     *
     * @param args no args, will be ignored
     */
    public static void main(String[] args) {

        // create db context
        // this automatically creates a new database file with the database schema (code-first-approach)
        // the first user becomes db admin

        try {

            // create roles if they don't exist
            // TODO: move this line to the handler that initializes a new h2 database

            createDbSchema();
            testDbAccountManagement();
            testPrivileges();

        } catch (Exception ex) {

            System.out.println("database test failed");
            ex.printStackTrace();

        } finally {

            // delete test database
            boolean success = new File("D:\\TEMP\\foo.h2.mv.db").delete();
            System.out.println("Deletion of test database: " + (success ? "successful" : "failed"));
        }
    }

    private static void createDbSchema() {

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            System.out.println("database schema creation successful");

        } catch (Exception ex) {

            System.out.println("database schema creation failed");
            ex.printStackTrace();
        }
    }

    private static void testDbAccountManagement() {

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new database accounts
            context.createDatabaseUser("TestAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("TestDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("TestViewer", "foobar", DbUserRole.Viewer);

            // test db accounts query
            List<DbUser> users = context.getDatabaseUsers();
            System.out.println("Db accounts test " + (users.size() == 3 ? "successful" : "failed"));
            users.forEach(System.out::println);

        } catch (Exception ex) {

            System.out.println("db account test failed");
            ex.printStackTrace();
        }
    }

    private static void testPrivileges() {

        // test privileges: getDatabaseUsers() X Admin
        try (ArtDbContext context = new ArtDbContext("TestAdmin", "foobar")) {

            // test switch user
            List<DbUser> users = context.getDatabaseUsers();
            System.out.println("privileges test successful (subject: getDatabaseUsers(), role: Admin)");

        } catch (Exception ex) {

            System.out.println("privileges test failed (subject: getDatabaseUsers(), role: Admin)");
            ex.printStackTrace();
        }

        // test privileges: getDatabaseUsers() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext("TestDataAnalyst", "foobar")) {

            // test switch user
            List<DbUser> users = context.getDatabaseUsers();
            System.out.println("privileges test failed (subject: getDatabaseUsers(), role: DataAnalyst)");
            users.forEach(System.out::println);

        } catch (Exception ex) {

            System.out.println("privileges test successful (subject: getDatabaseUsers(), role: DataAnalyst)");
            ex.printStackTrace();
        }

        // test privileges: getDatabaseUsers() X Viewer
        try (ArtDbContext context = new ArtDbContext("TestViewer", "foobar")) {

            List<DbUser> users = context.getDatabaseUsers();
            System.out.println("privileges test failed (subject: getDatabaseUsers(), role: Viewer)");
            users.forEach(System.out::println);

        } catch (Exception ex) {

            System.out.println("privileges test successful (subject: getDatabaseUsers(), role: Viewer)");
            ex.printStackTrace();
        }

        // TODO: add test for each role X database query on ArtDbContext
    }

}