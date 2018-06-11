package junit.localdb;

import data.entities.SapConfiguration;
import data.entities.Whitelist;
import data.localdb.ArtDbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SapSettingsTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testQuerySapConfig() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists
            List<SapConfiguration> configs = context.getSapConfigs();

            // check if test data was queried successfully
            ret = configs.size() == 1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("whitelist query test " + (ret ? "successful" : "failed"));
        assert(ret);
    }

    @Test
    public void testCreateSapConfig() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create a new data object
            SapConfiguration config = new SapConfiguration();

            // check if test data was queried successfully
            //ret = configs.size() == 1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("whitelist query test " + (ret ? "successful" : "failed"));
        assert(ret);
    }

    // TODO: implement tests

}
