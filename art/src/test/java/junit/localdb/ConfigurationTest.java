package junit.localdb;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testUpdateConfiguration() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            int configCount = context.getConfigs(true).size();
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(2)).findFirst().get();
            int configId = config.getId();

            // apply changes to config
            AccessPattern patternToRemove = config.getPatterns().stream().filter(x -> x.getId().equals(1)).findFirst().get();
            config.getPatterns().remove(patternToRemove);

            final String newName = "a new name";
            final String newDescription = "a new description";
            config.setName(newName);
            config.setDescription(newDescription);

            // update configs
            context.updateConfig(config);

            // test to see if the update worked
            Configuration updatedConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(configId)).findFirst().get();

            ret = updatedConfig.getPatterns().stream().noneMatch(x -> x.getId().equals(patternToRemove.getId()))
                && updatedConfig.getName().equals(newName)
                && updatedConfig.getDescription().equals(newDescription)
                && context.getConfigs(true).size() == configCount;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testUpdateConfigurationWithArchiving() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            int configCount = context.getConfigs(true).size();
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(1)).findFirst().get();
            int configId = config.getId();

            // apply changes to config
            AccessPattern patternToRemove = config.getPatterns().stream().filter(x -> x.getId().equals(1)).findFirst().get();
            config.getPatterns().remove(patternToRemove);

            final String newName = "a new name";
            final String newDescription = "a new description";
            config.setName(newName);
            config.setDescription(newDescription);

            // update configs
            context.updateConfig(config);

            // test to see if the update worked
            Configuration updatedConfig = context.getConfigs(false).stream().filter(x -> x.getId().equals(configId)).findFirst().get();

            ret = updatedConfig.getPatterns().stream().noneMatch(x -> x.getId().equals(patternToRemove.getId()))
                && updatedConfig.getName().equals(newName)
                && updatedConfig.getDescription().equals(newDescription)
                && context.getConfigs(true).size() == configCount + 1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testDeleteConfiguration() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query config
            int configCount = context.getConfigs(true).size();
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(2)).findFirst().get();

            // delete config
            context.deleteConfig(config);

            //test to see if the config is deleted
            ret = context.getConfigs(false).stream().noneMatch(x -> x.equals(config))
                && context.getConfigs(true).size() == configCount - 1;

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
            int configCount = context.getConfigs(true).size();
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(1)).findFirst().get();

            // delete config
            context.deleteConfig(config);

            //test to see if the config is deleted
            ret = context.getConfigs(false).stream().noneMatch(x -> x.equals(config))
                && context.getConfigs(true).size() == configCount;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);

    }

}
