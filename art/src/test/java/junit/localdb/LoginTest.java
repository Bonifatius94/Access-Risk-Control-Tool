package junit.localdb;

import data.localdb.ArtDbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testLogin() {

        assert (testLogin("TestDataAnalyst", "foobar", true));
        assert (testLogin("foo", "123", false));
    }

    private boolean testLogin(String username, String password, boolean expectedResult) {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test if login works
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

}
