package junit.localdb;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.Whitelist;
import data.localdb.ArtDbContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ConfigurationTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testQueryConfiguration() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            List<Configuration> configs = context.getConfigs(false);

            // check if test data was queried successfully
            ret = configs.size() == 2 && configs.stream().allMatch(x -> x.getPatterns().size() == 2);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testCreateConfiguration() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns and whitelist (from data seed)
            List<AccessPattern> patterns = context.getPatterns(false);
            Whitelist whitelist = context.getWhitelists(false).stream().findFirst().get();

            // create a new config
            final String configName = "a config name";
            final String configDescription = "a test description";

            Configuration config = new Configuration();
            config.setName(configName);
            config.setDescription(configDescription);
            config.setWhitelist(whitelist);

            config.setPatterns(Arrays.asList(
                    patterns.stream().filter(x -> x.getId().equals(new Integer(1))).findFirst().get(),
                    patterns.stream().filter(x -> x.getId().equals(new Integer(2))).findFirst().get()
            ));

            // insert config into database
            context.createConfig(config);

            // query updated data
            List<Configuration> configs = context.getConfigs(false);

            // check if test data was queried successfully
            ret = configs.size() == 3 && configs.stream().anyMatch(x -> x.getName().equals(configName) && x.getDescription().equals(configDescription) && x.getPatterns().size() == 2);

            // TODO: test if creation fails if the pattern does not exist (error is desired)

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    @Disabled
    public void testUpdateConfiguration() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            Configuration activeConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(1))).findFirst().get();
            Configuration archivedConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(3))).findFirst().get();

            // apply changes to configs
            AccessPattern patternToRemove = activeConfig.getPatterns().stream().filter(x -> x.getId().equals(new Integer(3))).findFirst().get();
            activeConfig.getPatterns().remove(patternToRemove);

            final String newName = "a new name";
            final String newDescription = "a new description";
            activeConfig.setName(newName);
            activeConfig.setDescription(newDescription);

            // update configs
            context.updateConfig(activeConfig);
            context.updateConfig(archivedConfig);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    @Disabled
    public void testUpdateConfigurationWithArchiving() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            Configuration activeConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(1))).findFirst().get();
            Configuration archivedConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(3))).findFirst().get();

            // apply changes to configs
            AccessPattern patternToRemove = activeConfig.getPatterns().stream().filter(x -> x.getId().equals(new Integer(3))).findFirst().get();
            activeConfig.getPatterns().remove(patternToRemove);

            final String newName = "a new name";
            final String newDescription = "a new description";
            activeConfig.setName(newName);
            activeConfig.setDescription(newDescription);

            // update configs
            context.updateConfig(activeConfig);
            context.updateConfig(archivedConfig);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (false);
    }

    @Test
    public void testDeleteConfiguration() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            Configuration activeConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(1))).findFirst().get();

            // delete config
            context.deleteConfig(activeConfig);

            //test to see if the config is deleted
            ret = !context.getConfigs(false).stream().anyMatch(x -> x.equals(activeConfig));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testDeleteConfigurationWithArchiving() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            Configuration archivedConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(3))).findFirst().get();

            // delete config
            context.deleteConfig(archivedConfig);

            //test to see if the config is deleted
            ret = !context.getConfigs(true).stream().anyMatch(x -> x.equals(archivedConfig));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }
}
