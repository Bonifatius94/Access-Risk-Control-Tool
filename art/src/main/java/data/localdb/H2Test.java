package data.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;

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
        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create roles if they don't exist
            // TODO: move this line to the handler that initializes a new h2 database

            System.out.println("database schema creation successful");

            // create new database accounts
            context.createDatabaseUser("TestAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("TestDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("TestViewer", "foobar", DbUserRole.Viewer);

            // test db accounts query
            List<DbUser> users = context.getDatabaseUsers();
            System.out.println("Db accounts test " + (users.size() == 3 ? "successful" : "failed"));
            users.forEach(System.out::println);

        } catch (Exception ex) {

            System.out.println("database test failed");
            ex.printStackTrace();
        }
    }

}