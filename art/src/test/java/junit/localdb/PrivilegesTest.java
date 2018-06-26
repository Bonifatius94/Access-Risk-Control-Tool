package junit.localdb;

import data.entities.DbUser;
import data.localdb.ArtDbContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

@SuppressWarnings("all")
public class PrivilegesTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    @Disabled
    public void testPrivileges() {

        // IArtDbContext.getDatabaseUsers()
        assert (testPrivilegesGetDatabaseUsers("TestAdmin", "foobar", true));
        assert (testPrivilegesGetDatabaseUsers("TestDataAnalyst", "foobar", false));
        assert (testPrivilegesGetDatabaseUsers("TestViewer", "foobar", false));

        assert (false);

        // TODO: add calls for missing database operations here ......
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
