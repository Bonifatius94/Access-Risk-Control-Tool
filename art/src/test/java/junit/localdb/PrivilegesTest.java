package junit.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class PrivilegesTest {

    @BeforeAll
    public static void initTests() {

        System.out.print("cleaning up datebase ...");
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");
        System.out.println(" done");

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new database accounts
            context.createDatabaseUser("TestAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("TestDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("TestViewer", "foobar", DbUserRole.Viewer);

        } catch (Exception ex) {
            assert(false);
        }
    }

    private static void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

    @Test
    public void testPrivileges() throws Exception {

        // IArtDbContext.getDatabaseUsers()
        assert(testPrivilegesGetDatabaseUsers("TestAdmin", "foobar", true));
        assert(testPrivilegesGetDatabaseUsers("TestDataAnalyst", "foobar", false));
        assert(testPrivilegesGetDatabaseUsers("TestViewer", "foobar", false));

        // TODO: add calls for missing database operations here ...
    }

    private boolean testPrivilegesGetDatabaseUsers(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getDatabaseUsers() X Admin
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<DbUser> users = context.getDatabaseUsers();
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    // TODO: add a test method for each database operation on IArtDbContext and call it for all three user roles in testPrivileges()

}
