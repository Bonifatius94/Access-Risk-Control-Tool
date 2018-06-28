package junit.localdb;

import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

@SuppressWarnings("all")
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
            List<SapConfiguration> configs = context.getSapConfigs(false);

            // check if test data was queried successfully
            ret = configs.size() == 1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testCreateSapConfig() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create a new data object
            final String poolCapacity = "1";
            SapConfiguration sapConfig = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", poolCapacity);

            // insert into database
            context.createSapConfig(sapConfig);

            // query updated data
            List<SapConfiguration> configs = context.getSapConfigs(false);

            // check if config was inserted successfully
            ret = configs.size() == 2 && configs.stream().anyMatch(x -> x.getPoolCapacity().equals(poolCapacity));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testUpdateSapConfig() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query sap config
            SapConfiguration config = context.getSapConfigs(false).stream().findFirst().get();
            Integer id = config.getId();

            // apply changes to sap config
            final String newLanguage = "DE";
            config.setLanguage(newLanguage);

            // insert into database
            context.updateSapConfig(config);

            // query updated data
            config = context.getSapConfigs(false).stream().filter(x -> x.getId().equals(id)).findFirst().get();

            // check if config was inserted successfully
            ret = config.getLanguage().equals(newLanguage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testUpdateSapConfigWithArchiving() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query sap config
            SapConfiguration config = context.getSapConfigs(true).stream().filter(x -> x.isArchived() == true).findFirst().get();
            Integer id = config.getId();

            // apply changes to sap config
            final String newLanguage = "DE";
            config.setLanguage(newLanguage);

            // insert into database
            context.updateSapConfig(config);

            // query updated data
            config = context.getSapConfigs(true).stream().filter(x -> x.getId().equals(id)).findFirst().get();

            // check if config was inserted successfully
            ret = config.getLanguage().equals(newLanguage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testDeleteSapConfig() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query sap config
            SapConfiguration config = context.getSapConfigs(false).stream().findFirst().get();
            Integer id = config.getId();

            // insert into database
            context.deleteSapConfig(config);

            // query updated data
            config = context.getSapConfigs(false).stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);

            // check if config was inserted successfully
            ret = config == null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testDeleteSapConfigWithArchiving() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query sap config
            SapConfiguration config = context.getSapConfigs(true).stream().filter(x -> x.isArchived() == true).findFirst().get();
            Integer id = config.getId();

            // insert into database
            context.deleteSapConfig(config);

            // query updated data
            config = context.getSapConfigs(true).stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);

            // check if config was inserted successfully
            ret = config.isArchived() == true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

}
