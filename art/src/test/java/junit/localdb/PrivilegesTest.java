package junit.localdb;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.DbUser;
import data.entities.DbUserRole;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import data.localdb.ArtDbContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
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

        // ============================================
        //                 C R E A T E
        // ============================================

        //IArtDbContext.createSapQuery()
        assert (testPrivilegesCreateSapQuery("TestAdmin", "foobar", false));
        assert (testPrivilegesCreateSapQuery("TestConfigurator", "foobar", false));
        assert (testPrivilegesCreateSapQuery("TestViewer", "foobar", true));

        //IArtDbContext.createConfig()
        assert (testPrivilegesCreateConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesCreateConfig("TestConfigurator", "foobar", true));
        assert (testPrivilegesCreateConfig("TestViewer", "foobar", false));

        //IArtDbContext.createPattern()
        assert (testPrivilegesCreatePattern("TestAdmin", "foobar", false));
        assert (testPrivilegesCreatePattern("TestConfigurator", "foobar", true));
        assert (testPrivilegesCreatePattern("TestViewer", "foobar", false));

        //IArtDbContext.createWhitelist()
        assert (testPrivilegesCreateWhitelist("TestAdmin", "foobar", false));
        assert (testPrivilegesCreateWhitelist("TestConfigurator", "foobar", true));
        assert (testPrivilegesCreateWhitelist("TestViewer", "foobar", false));

        //IArtDbContext.createSapConfig()
        assert (testPrivilegesGetSapQueries("TestAdmin", "foobar", false));
        assert (testPrivilegesGetSapQueries("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetSapQueries("TestViewer", "foobar", false));

        // ============================================
        //                   R E A D
        // ============================================

        //IArtDbContext.getSapQueries()
        assert (testPrivilegesCreateSapQuery("TestAdmin", "foobar", false));
        assert (testPrivilegesCreateSapQuery("TestConfigurator", "foobar", false));
        assert (testPrivilegesCreateSapQuery("TestViewer", "foobar", true));

        //IArtDbContext.getConfigs()
        assert (testPrivilegesGetConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetConfigs("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetConfigs("TestViewer", "foobar", true));

        //IArtDbContext.getPatterns()
        assert (testPrivilegesGetPatterns("TestAdmin", "foobar", false));
        assert (testPrivilegesGetPatterns("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetPatterns("TestViewer", "foobar", true));

        //IArtDbContext.getWhitelists()
        assert (testPrivilegesGetWhitelists("TestAdmin", "foobar", false));
        assert (testPrivilegesGetWhitelists("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetWhitelists("TestViewer", "foobar", true));

        //IArtDbContext.getSapConfigs()
        assert (testPrivilegesGetSapConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetSapConfigs("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetSapConfigs("TestViewer", "foobar", true));

        // IArtDbContext.getDatabaseUsers()
        assert (testPrivilegesGetDatabaseUsers("TestAdmin", "foobar", true));
        assert (testPrivilegesGetDatabaseUsers("TestConfigurator", "foobar", false));
        assert (testPrivilegesGetDatabaseUsers("TestViewer", "foobar", false));

        // ============================================
        //               F I L T E R S
        // ============================================

        //IArtDbContext.getFilteredPatterns()
        assert (testPrivilegesGetFilteredPatterns("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredPatterns("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetFilteredPatterns("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredWhitelists()
        assert (testPrivilagesGetFilteredWhitelists("TestAdmin", "foobar", false));
        assert (testPrivilagesGetFilteredWhitelists("TestConfigurator", "foobar", true));
        assert (testPrivilagesGetFilteredWhitelists("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredSapConfigs()
        assert (testPrivilegesGetFilteredSapConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredSapConfigs("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetFilteredSapConfigs("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredConfigs()
        assert (testPrivilegesGetFilteredConfigs("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredConfigs("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetFilteredConfigs("TestViewer", "foobar", true));

        //IArtDbContext.getFilteredCriticalAccessQueries()
        assert (testPrivilegesGetFilteredCriticalAccessQueries("TestAdmin", "foobar", false));
        assert (testPrivilegesGetFilteredCriticalAccessQueries("TestConfigurator", "foobar", true));
        assert (testPrivilegesGetFilteredCriticalAccessQueries("TestViewer", "foobar", true));

        // ============================================
        //                 U P D A T E
        // ============================================

        //IArtDbContext.updateConfig()
        assert (testPrivilegesUpdateConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdateConfig("TestConfigurator", "foobar", true));
        assert (testPrivilegesUpdateConfig("TestViewer", "foobar", false));

        //IArtDbContext.updatePattern()
        assert (testPrivilegesUpdatePattern("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdatePattern("TestConfigurator", "foobar", true));
        assert (testPrivilegesUpdatePattern("TestViewer", "foobar", false));

        //IArtDbContext.updateWhitelist()
        assert (testPrivilegesUpdateWhitelist("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdateWhitelist("TestConfigurator", "foobar", true));
        assert (testPrivilegesUpdateWhitelist("TestViewer", "foobar", false));

        //IArtDbContext.updateSapConfig()
        assert (testPrivilegesUpdateSapConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesUpdateSapConfig("TestConfigurator", "foobar", true));
        assert (testPrivilegesUpdateSapConfig("TestViewer", "foobar", false));

        // ============================================
        //                 D E L E T E
        // ============================================

        //IArtDbContext.deleteConfig()
        assert (testPrivilegesDeleteConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesDeleteConfig("TestConfigurator", "foobar", true));
        assert (testPrivilegesDeleteConfig("TestViewer", "foobar", false));

        //IArtDbContext.deletePattern()
        assert (testPrivilegesDeletePattern("TestAdmin", "foobar", false));
        assert (testPrivilegesDeletePattern("TestConfigurator", "foobar", true));
        assert (testPrivilegesDeletePattern("TestViewer", "foobar", false));

        //IArtDbContext.deleteWhitelist()
        assert (testPrivilegesDeleteWhitelist("TestAdmin", "foobar", false));
        assert (testPrivilegesDeleteWhitelist("TestConfigurator", "foobar", true));
        assert (testPrivilegesDeleteWhitelist("TestViewer", "foobar", false));

        //IArtDbContext.deleteSapConfig()
        assert (testPrivilegesDeleteSapConfig("TestAdmin", "foobar", false));
        assert (testPrivilegesDeleteSapConfig("TestConfigurator", "foobar", true));
        assert (testPrivilegesDeleteSapConfig("TestViewer", "foobar", false));

        // ============================================
        //          U S E R   A C C O U N T S
        // ============================================

        //IArtDbContext.createDatabaseUser()
        assert (testPrivilegesCreateDatabaseUser("TestAdmin", "foobar", true));
        assert (testPrivilegesCreateDatabaseUser("TestConfigurator", "foobar", false));
        assert (testPrivilegesCreateDatabaseUser("TestViewer", "foobar", false));

        //IArtDbContext.updateUserRoles()
        assert (testPrivilegesUpdateUserRoles("TestAdmin", "foobar", true));
        assert (testPrivilegesUpdateUserRoles("TestConfigurator", "foobar", false));
        assert (testPrivilegesUpdateUserRoles("TestViewer", "foobar", false));

        //IArtDbContext.changePassword()
        assert (testPrivilegesChangePassword("TestAdmin", "foobar", true));
        assert (testPrivilegesChangePassword("TestConfigurator", "foobar", false));
        assert (testPrivilegesChangePassword("TestViewer", "foobar", false));

        //IArtDbContext.deleteDatabaseUser()
        assert (testPrivilegesDeleteDatabaseUser("TestAdmin", "foobar", true));
        assert (testPrivilegesDeleteDatabaseUser("TestConfigurator", "foobar", false));
        assert (testPrivilegesDeleteDatabaseUser("TestViewer", "foobar", false));

        //IArtDbContext.getCurrentUser()
        assert (testPrivilegesGetCurrentUser("TestAdmin", "foobar", true));
        assert (testPrivilegesGetCurrentUser("TestConfigurator", "foobar", false));
        assert (testPrivilegesGetCurrentUser("TestViewer", "foobar", false));

        //IArtDbContext.setFirstLoginOfCurrentUser()
        assert (testPrivilegesSetFirstLoginOfCurrentUser("TestAdmin", "foobar", true));
        assert (testPrivilegesSetFirstLoginOfCurrentUser("TestConfigurator", "foobar", false));
        assert (testPrivilegesSetFirstLoginOfCurrentUser("TestViewer", "foobar", false));

    }

    // ============================================
    //                 C R E A T E
    // ============================================

    private boolean testPrivilegesCreateSapQuery(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createSapQuery() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.createSapQuery(new CriticalAccessQuery());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreateConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createConfig() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.createConfig(new Configuration());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreatePattern(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createPattern() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.createPattern(new AccessPattern());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreateWhitelist(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createWhitelist() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.createWhitelist(new Whitelist());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesCreateSapConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: createSapConfig() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.createSapConfig(new SapConfiguration());
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

        // test privileges: updateConfig() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.updateConfig(new Configuration());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesUpdatePattern(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updatePattern() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.updatePattern(new AccessPattern());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesUpdateWhitelist(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updateWhitelist() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.updateWhitelist(new Whitelist());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesUpdateSapConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: updateSapConfig() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.updateSapConfig(new SapConfiguration());
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

        // test privileges: deleteConfig() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.deleteConfig(new Configuration());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesDeletePattern(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deletePattern() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.deletePattern(new AccessPattern());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesDeleteWhitelist(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deleteWhitelist() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.deleteWhitelist(new Whitelist());
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        return (ret == expectedResult);
    }

    private boolean testPrivilegesDeleteSapConfig(String username, String password, boolean expectedResult) {

        boolean ret = false;

        // test privileges: deleteSapConfig() X Configurator
        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // test switch user
            context.deleteSapConfig(new SapConfiguration());
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
            context.updateUserRoles(new DbUser("tset", new HashSet<DbUserRole>()));
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
