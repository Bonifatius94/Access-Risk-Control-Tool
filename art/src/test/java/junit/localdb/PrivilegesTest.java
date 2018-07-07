package junit.localdb;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.ConditionLinkage;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.DbUser;
import data.entities.DbUserRole;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;
import data.localdb.ArtDbContext;

import io.msoffice.excel.WhitelistImportHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("all")
public class PrivilegesTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testPrivileges() {

        // ============================================
        //                 C R E A T E
        // ============================================

        //IArtDbContext.createSapQuery()
        //assert (testPrivilegesCreateSapQuery("TestAdmin", "foobar", false));
        //assert (testPrivilegesCreateSapQuery("TestDataAnalyst", "foobar", false));
        assert (testPrivilegesCreateSapQuery("TestViewer", "foobar", true));

        //IArtDbContext.createConfig()
        //assert (testPrivilegesCreateConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesCreateConfig("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesCreateConfig("TestViewer", "foobar", false));

        //IArtDbContext.createPattern()
        //assert (testPrivilegesCreatePattern("TestAdmin", "foobar", false));
        assert (testPrivilegesCreatePattern("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesCreatePattern("TestViewer", "foobar", false));

        //IArtDbContext.createWhitelist()
        //assert (testPrivilegesCreateWhitelist("TestAdmin", "foobar", false));
        assert (testPrivilegesCreateWhitelist("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesCreateWhitelist("TestViewer", "foobar", false));

        //IArtDbContext.createSapConfig()
        //assert (testPrivilegesGetSapQueries("TestAdmin", "foobar", false));
        assert (testPrivilegesGetSapQueries("TestDataAnalyst", "foobar", true));
        //assert (testPrivilegesGetSapQueries("TestViewer", "foobar", false));

        // ============================================
        //                   R E A D
        // ============================================

        //IArtDbContext.getSapQueries()
        //assert (testPrivilegesCreateSapQuery("TestAdmin", "foobar", false));
        //assert (testPrivilegesCreateSapQuery("TestDataAnalyst", "foobar", false));
        assert (testPrivilegesCreateSapQuery("TestViewer", "foobar", true));

        //IArtDbContext.getConfigs()
        //assert (testPrivilegesGetConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetConfigs("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetConfigs("TestViewer", "foobar", true));

        //IArtDbContext.getPatterns()
        //assert (testPrivilegesGetPatterns("TestAdmin", "foobar", false));
        assert (testPrivilegesGetPatterns("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetPatterns("TestViewer", "foobar", true));

        //IArtDbContext.getWhitelists()
        //assert (testPrivilegesGetWhitelists("TestAdmin", "foobar", false));
        assert (testPrivilegesGetWhitelists("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetWhitelists("TestViewer", "foobar", true));

        //IArtDbContext.getSapConfigs()
        //assert (testPrivilegesGetSapConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetSapConfigs("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetSapConfigs("TestViewer", "foobar", true));

        // IArtDbContext.getDatabaseUsers()
        assert (testPrivilegesGetDatabaseUsers("TestAdmin", "foobar", true));
        //assert (testPrivilegesGetDatabaseUsers("TestDataAnalyst", "foobar", false));
        //assert (testPrivilegesGetDatabaseUsers("TestViewer", "foobar", false));

        // ============================================
        //               F I L T E R S
        // ============================================

        //IArtDbContext.getFilteredPatterns()
        //assert (testPrivilegesGetFilteredPatterns("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredPatterns("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetFilteredPatterns("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredWhitelists()
        //assert (testPrivilagesGetFilteredWhitelists("TestAdmin", "foobar", false));
        assert (testPrivilagesGetFilteredWhitelists("TestDataAnalyst", "foobar", true));
        assert (testPrivilagesGetFilteredWhitelists("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredSapConfigs()
        //assert (testPrivilegesGetFilteredSapConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredSapConfigs("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetFilteredSapConfigs("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredConfigs()
        //assert (testPrivilegesGetFilteredConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredConfigs("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetFilteredConfigs("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredCriticalAccessQueries()
        //assert (testPrivilegesGetFilteredCriticalAccessQueries("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredCriticalAccessQueries("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesGetFilteredCriticalAccessQueries("TestViewer", "foobar", true));

        // ============================================
        //                 U P D A T E
        // ============================================

        //IArtDbContext.updateConfig()
        //assert (testPrivilegesUpdateConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdateConfig("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesUpdateConfig("TestViewer", "foobar", false));

        //IArtDbContext.updatePattern()
        //assert (testPrivilegesUpdatePattern("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdatePattern("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesUpdatePattern("TestViewer", "foobar", false));

        //IArtDbContext.updateWhitelist()
        //assert (testPrivilegesUpdateWhitelist("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdateWhitelist("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesUpdateWhitelist("TestViewer", "foobar", false));

        //IArtDbContext.updateSapConfig()
        //assert (testPrivilegesUpdateSapConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdateSapConfig("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesUpdateSapConfig("TestViewer", "foobar", false));

        // ============================================
        //                 D E L E T E
        // ============================================

        //IArtDbContext.deleteConfig()
        //assert (testPrivilegesDeleteConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesDeleteConfig("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesDeleteConfig("TestViewer", "foobar", false));

        //IArtDbContext.deletePattern()
        //assert (testPrivilegesDeletePattern("TestAdmin", "foobar", false));
        assert (testPrivilegesDeletePattern("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesDeletePattern("TestViewer", "foobar", false));

        //IArtDbContext.deleteWhitelist()
        //assert (testPrivilegesDeleteWhitelist("TestAdmin", "foobar", false));
        assert (testPrivilegesDeleteWhitelist("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesDeleteWhitelist("TestViewer", "foobar", false));

        //IArtDbContext.deleteSapConfig()
        //assert (testPrivilegesDeleteSapConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesDeleteSapConfig("TestDataAnalyst", "foobar", true));
        assert (testPrivilegesDeleteSapConfig("TestViewer", "foobar", false));

        // ============================================
        //          U S E R   A C C O U N T S
        // ============================================

        //IArtDbContext.createDatabaseUser()
        assert (testPrivilegesCreateDatabaseUser("TestAdmin", "foobar", true));
        assert (testPrivilegesCreateDatabaseUser("TestDataAnalyst", "foobar", false));
        assert (testPrivilegesCreateDatabaseUser("TestViewer", "foobar", false));

        //IArtDbContext.updateUserRoles()
        assert (testPrivilegesUpdateUserRoles("TestAdmin", "foobar", true));
        //assert (testPrivilegesUpdateUserRoles("TestDataAnalyst", "foobar", false));
        //assert (testPrivilegesUpdateUserRoles("TestViewer", "foobar", false));

        //IArtDbContext.changePassword()
        assert (testPrivilegesChangePassword("TestAdmin", "foobar", true));
        assert (testPrivilegesChangePassword("TestDataAnalyst", "foobar", false));
        assert (testPrivilegesChangePassword("TestViewer", "foobar", false));

        //IArtDbContext.deleteDatabaseUser()
        assert (testPrivilegesDeleteDatabaseUser("TestAdmin", "foobar", true));
        assert (testPrivilegesDeleteDatabaseUser("TestDataAnalyst", "foobar", false));
        assert (testPrivilegesDeleteDatabaseUser("TestViewer", "foobar", false));

        //IArtDbContext.getCurrentUser()
        assert (testPrivilegesGetCurrentUser("TestAdmin", "foobar", true));
        //assert (testPrivilegesGetCurrentUser("TestDataAnalyst", "foobar", false));
        //assert (testPrivilegesGetCurrentUser("TestViewer", "foobar", false));

        //IArtDbContext.setFirstLoginOfCurrentUser()
        assert (testPrivilegesSetFirstLoginOfCurrentUser("TestAdmin", "foobar", true));
        //assert (testPrivilegesSetFirstLoginOfCurrentUser("TestDataAnalyst", "foobar", false));
        //assert (testPrivilegesSetFirstLoginOfCurrentUser("TestViewer", "foobar", false));

    }

    // ============================================
    //                 C R E A T E
    // ============================================

    private boolean testPrivilegesCreateSapQuery(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createSapQuery() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // retrieve config + sap config (used by the new query)
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(1)).findFirst().get();
            SapConfiguration sapConfig = context.getSapConfigs(false).stream().filter(x -> x.getId().equals(1)).findFirst().get();

            // prepare query entries
            AccessPattern violatedPattern = config.getPatterns().stream().filter(x -> x.getId().equals(3)).findFirst().get();
            Set entries = new HashSet();
            final String criticalUser = "raboof";
            CriticalAccessEntry entry = new CriticalAccessEntry(violatedPattern, criticalUser);
            entries.add(entry);

            // create the query
            context.createSapQuery(new CriticalAccessQuery(config, sapConfig, entries));
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreateConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createConfig() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

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
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreatePattern(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createPattern() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // create pattern data objects
            final String usecaseId = "1.A";
            final String description = "a test description";

            AccessPattern pattern = new AccessPattern(usecaseId, description, new ArrayList<>(), ConditionLinkage.And);

            pattern.setConditions(Arrays.asList(
                new AccessCondition(
                    pattern,
                    new AccessPatternCondition(Arrays.asList(
                        new AccessPatternConditionProperty("S_TCODE", "TCD", "SCCL", null, null, null),
                        new AccessPatternConditionProperty("S_ADMI_FCD", "S_ADMI_FCD", "T000", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "ACTVT", "02", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "DICBERCLS", "\"*\"", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_CLI", "CLIIDMAINT", "X", null, null, null)
                    ))
                ),
                new AccessCondition(
                    pattern,
                    new AccessPatternCondition(Arrays.asList(
                        new AccessPatternConditionProperty("S_TCODE", "TCD", "SCCL", null, null, null),
                        new AccessPatternConditionProperty("S_ADMI_FCD", "S_ADMI_FCD", "T000", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "ACTVT", "02", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "DICBERCLS", "\"*\"", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_CLI", "CLIIDMAINT", "X", null, null, null)
                    ))
                )
            ));

            // insert pattern into database
            context.createPattern(pattern);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreateWhitelist(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createWhitelist() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // query whitelists before insertion
            List<Whitelist> whitelists = context.getWhitelists(false);
            int countBefore = (whitelists != null) ? whitelists.size() : 0;

            // insert a whitelist through the whitelistHelper
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            whitelist.setName("a test name");
            whitelist.setDescription("a test description");

            //  System.out.println(whitelist);
            context.createWhitelist(whitelist);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreateSapConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createSapConfig() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // create a new data object
            final String poolCapacity = "1";
            SapConfiguration sapConfig = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", poolCapacity);

            // insert into database
            context.createSapConfig(sapConfig);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    // ============================================
    //                   R E A D
    // ============================================

    private boolean testPrivilegesGetSapQueries(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getSapQueries() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<CriticalAccessQuery> sapQueriesFalse = context.getSapQueries(false);
            List<CriticalAccessQuery> sapQueriesTrue = context.getSapQueries(true);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetConfigs(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getConfigs() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<Configuration> ConfigsFalse = context.getConfigs(false);
            List<Configuration> ConfigsTrue = context.getConfigs(true);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetPatterns(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getPatterns() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<AccessPattern> patternsFalse = context.getPatterns(false);
            List<AccessPattern> patternsTrue = context.getPatterns(true);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetWhitelists(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getWhitelists() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<Whitelist> whitelistsFalse = context.getWhitelists(false);
            List<Whitelist> whitelistsTrue = context.getWhitelists(true);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetSapConfigs(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getSapConfigs() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<SapConfiguration> sapConfigsFalse = context.getSapConfigs(false);
            List<SapConfiguration> sapConfigsTrue = context.getSapConfigs(true);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
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

    // ============================================
    //               F I L T E R S
    // ============================================

    private boolean testPrivilegesGetFilteredPatterns(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getFilteredPatterns() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<AccessPattern> filteredPatternsFalse = context.getFilteredPatterns(false, null, null, null, null);
            List<AccessPattern> filteredPatternsTrue = context.getFilteredPatterns(true, null, null, null, null);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilagesGetFilteredWhitelists(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getFilteredWhitelists() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<Whitelist> filteredWhitelistsFalse = context.getFilteredWhitelists(false, null, null, null, null);
            List<Whitelist> filteredWhitelistsTrue = context.getFilteredWhitelists(true, null, null, null, null);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetFilteredSapConfigs(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getFilteredSapConfigs() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<SapConfiguration> filteredSapConfigsFalse = context.getFilteredSapConfigs(false, null, null, null, null);
            List<SapConfiguration> filteredSapConfigsTrue = context.getFilteredSapConfigs(true, null, null, null, null);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetFilteredConfigs(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getFilteredConfigs() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<Configuration> filteredConfigsFalse = context.getFilteredConfigs(false, null, null, null, null);
            List<Configuration> filteredConfigsTrue = context.getFilteredConfigs(true, null, null, null, null);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetFilteredCriticalAccessQueries(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getFilteredCriticalAccessQueries() X Viewer
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            List<CriticalAccessQuery> filteredCriticalAccessQueriesFalse = context.getFilteredCriticalAccessQueries(false, null, null, null, null);
            List<CriticalAccessQuery> filteredCriticalAccessQueriesTrue = context.getFilteredCriticalAccessQueries(true, null, null, null, null);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    // ============================================
    //                 U P D A T E
    // ============================================

    private boolean testPrivilegesUpdateConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updateConfig() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

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
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesUpdatePattern(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updatePattern() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            int patternCount = context.getPatterns(true).size();
            AccessPattern profilePattern = context.getPatterns(false).stream().filter(x -> x.getId().equals(7)).findFirst().get();
            AccessProfileCondition profileCondition = profilePattern.getConditions().stream().findFirst().get().getProfileCondition();
            Integer profilePatternId = profilePattern.getId();

            // apply changes to profile condition
            final String newProfile = "SAP_ALL";
            final String newDescription = "another description";
            profilePattern.setLinkage(ConditionLinkage.Or);
            profilePattern.setDescription(newDescription);
            profileCondition.setProfile(newProfile);

            // update database
            context.updatePattern(profilePattern);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesUpdateWhitelist(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updateWhitelist() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // query first whitelist from database
            int whitelistsCount = context.getWhitelists(true).size();
            Whitelist whitelist = context.getWhitelists(false).stream().filter(x -> x.getId().equals(3)).findFirst().get();
            int whitelistId = whitelist.getId();

            // make changes
            final String newDescription = "another description";
            whitelist.setDescription(newDescription);

            List<WhitelistEntry> entries = new ArrayList<>(whitelist.getEntries());
            WhitelistEntry entry1 = entries.get(0);
            int entry1Id = entry1.getId();
            entries.remove(1);

            final String newUsername = "foo123";
            final String newUsecaseId = "blabla";
            entry1.setUsername(newUsername);
            entry1.setUsecaseId(newUsecaseId);

            whitelist.setEntries(entries);

            // update whitelist (archive should be false)
            context.updateWhitelist(whitelist);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesUpdateSapConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updateSapConfig() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // query sap config
            int sapConfigsCount = context.getSapConfigs(true).size();
            SapConfiguration config = context.getSapConfigs(false).stream().filter(x -> x.getId().equals(3)).findFirst().get();
            Integer id = config.getId();

            // apply changes to sap config
            final String newLanguage = "DE";
            config.setLanguage(newLanguage);

            // insert into database
            context.updateSapConfig(config);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    // ============================================
    //                 D E L E T E
    // ============================================

    private boolean testPrivilegesDeleteConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deleteConfig() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // query config
            int configCount = context.getConfigs(true).size();
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(2)).findFirst().get();

            // delete config
            context.deleteConfig(config);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesDeletePattern(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deletePattern() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // query patterns
            int patternCount = context.getPatterns(true).size();
            List<AccessPattern> patterns = context.getPatterns(false);
            AccessPattern profilePattern = patterns.stream().filter(x -> x.getId().equals(7)).findFirst().get();
            AccessPattern multiConditionPattern = patterns.stream().filter(x -> x.getId().equals(8)).findFirst().get();

            // delete patterns
            context.deletePattern(profilePattern);
            context.deletePattern(multiConditionPattern);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesDeleteWhitelist(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deleteWhitelist() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // query whitelists
            int whitelistsCount = context.getWhitelists(true).size();
            Whitelist whitelist = context.getWhitelists(false).stream().filter(x -> x.getId().equals(3)).findFirst().get();
            int id = whitelist.getId();

            // delete whitelist
            context.deleteWhitelist(whitelist);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesDeleteSapConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deleteSapConfig() X DataAnalyst
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // query sap config
            int sapConfigsCount = context.getSapConfigs(true).size();
            SapConfiguration config = context.getSapConfigs(false).stream().filter(x -> x.getId().equals(3)).findFirst().get();
            Integer id = config.getId();

            // insert into database
            context.deleteSapConfig(config);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    // ============================================
    //          U S E R   A C C O U N T S
    // ============================================

    private boolean testPrivilegesCreateDatabaseUser(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createDatabaseUser() X Admin
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.createDatabaseUser(new DbUser("tset", new HashSet<DbUserRole>()), "tset");
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesUpdateUserRoles(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updateUserRoles() X Admin
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.updateUserRoles(new DbUser("tset", false, true, false, false));
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesChangePassword(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: changePassword() X Admin
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.changePassword("tset", "tset");
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesDeleteDatabaseUser(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deleteDatabaseUser() X Admin
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.deleteDatabaseUser("tset");
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesGetCurrentUser(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: getCurrentUser() X Admin
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.getCurrentUser();
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesSetFirstLoginOfCurrentUser(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: setFirstLoginOfCurrentUser() X Admin
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.setFirstLoginOfCurrentUser(new DbUser("tset", new HashSet<DbUserRole>()), true);
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

}
