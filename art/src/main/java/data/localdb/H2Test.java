package data.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;
import data.entities.Whitelist;
import io.msoffice.excel.WhitelistImportHelper;

import java.io.File;
import java.util.List;

public class H2Test {

    /**
     * This method runs a test program.
     *
     * @param args no args, will be ignored
     */
    public static void main(String[] args) {

        // delete database if it exists
        File database = new File("D:\\TEMP\\foo.h2.mv.db");

        if (database.exists()) {
            database.delete();
        }

        // create db context
        // this automatically creates a new database file with the database schema (code-first-approach)
        // the first user becomes db admin

        try {

            // create roles if they don't exist
            // TODO: move this line to the handler that initializes a new h2 database

            createDbSchema();
            testDbAccountManagement();
            testPrivileges();

            testCreateWhitelist();

        } catch (Exception ex) {

            System.out.println("database test failed");
            ex.printStackTrace();
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

    private static void testCreateWhitelist() throws Exception {

        Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
        System.out.println(whitelist);

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists before insertion
            List<Whitelist> whitelists = context.getWhitelists();
            int countBefore = (whitelists != null) ? whitelists.size() : 0;
            System.out.println("whitelists count before insertion: " + countBefore);
            System.out.println();

            // insert another whitelist
            context.createWhitelist(whitelist);

            // query whitelists after insertion
            whitelists = context.getWhitelists();
            int countAfter = (whitelists != null) ? whitelists.size() : 0;
            System.out.println("whitelists count after insertion: " + countAfter);
            System.out.println();
            System.out.println(whitelists.get(0));

            // compare counts
            System.out.println("whitelist creation test " + (countBefore + 1 == countAfter ? "successful" : "failed"));

        } catch (Exception ex) {

            System.out.println("whitelist creation test failed");
            ex.printStackTrace();
        }
    }

}