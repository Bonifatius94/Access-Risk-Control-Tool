package data.localdb;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.DbUserRole;
import data.entities.SapConfiguration;
import data.entities.Whitelist;

import java.util.List;

@SuppressWarnings("unused")
public interface IArtDbContext {

    // ============================================
    //                 C R E A T E
    // ============================================

    /**
     * This method writes the results of an already executed sap query to the local database. All referenced table entries are also created.
     *
     * @param query the query to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void createSapQuery(CriticalAccessQuery query) throws Exception;

    /**
     * This method creates a new configuration in the local database. No referenced table entries are created.
     *
     * @param config the configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void createConfig(Configuration config) throws Exception;

    /**
     * This method creates a new access pattern in the local database. All referenced table entries are also created.
     *
     * @param pattern the access pattern to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void createPattern(AccessPattern pattern) throws Exception;

    /**
     * This method creates a new whitelist in the local database. All referenced table entries are also created.
     *
     * @param whitelist the whitelist to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void createWhitelist(Whitelist whitelist) throws Exception;

    /**
     * This method creates a new sap configuration in the local database. All referenced table entries are also created.
     *
     * @param config the sap configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void createSapConfig(SapConfiguration config) throws Exception;

    // ============================================
    //                   R E A D
    // ============================================

    /**
     * This method selects all already executed ART queries from the local database.
     * TODO: add filter option (e.g. by time: today, last week, last month, last year, all)
     *
     * @return a list of already executed ART queries
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<CriticalAccessQuery> getArtQueries() throws Exception;

    /**
     * This method selects all configurations from the local database that are not archived with history flag.
     *
     * @return a list of all configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<Configuration> getConfigs() throws Exception;

    /**
     * This method selects all access patterns from the local database that are not archived with history flag.
     *
     * @return a list of all access patterns
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<AccessPattern> getPatterns() throws Exception;

    /**
     * This method selects all whitelists from the local database that are not archived with history flag.
     *
     * @return a list of all whitelists
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<Whitelist> getWhitelists() throws Exception;

    /**
     * This method selects all sap configurations from the local database that are not archived with history flag.
     *
     * @return a list of all sap configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<SapConfiguration> getSapConfigs() throws Exception;

    /**
     * This method selects all user names of existing local database accounts and their privileges.
     *
     * @return a list of local database users
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<String> getDatabaseUsers() throws Exception;

    // TODO: add some userful views for more complicated queries

    // ============================================
    //                 U P D A T E
    // ============================================

    /**
     * This method updates all properties referenced in the config.
     * If the config has already been used in sap queries, the history flag is set and the changes are applied to a new
     *
     * @param config the config to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void updateConfig(Configuration config) throws Exception;

    /**
     * This method updates all properties referenced in the access pattern.
     *
     * @param pattern the access pattern to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void updatePattern(AccessPattern pattern) throws Exception;

    /**
     * This method updates all properties referenced in the whitelist.
     *
     * @param whitelist the whitelist to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void updateWhitelist(Whitelist whitelist) throws Exception;

    /**
     * This method updates all properties referenced in the config.
     *
     * @param config the config to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void updateSapConfig(SapConfiguration config) throws Exception;

    // ============================================
    //                 D E L E T E
    // ============================================

    /**
     * This method deletes an existing configuration from the local database. Referenced table entries are not deleted.
     * If the config has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param config the configuration to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void deleteConfig(Configuration config) throws Exception;

    /**
     * This method deletes an existing access pattern from the local database. Referenced table entries are also deleted.
     * If the pattern has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param pattern the access pattern to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void deletePattern(AccessPattern pattern) throws Exception;

    /**
     * This method deletes an existing whitelist from the local database. Referenced table entries are also deleted.
     * If the whitelist has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param whitelist the whitelist to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void deleteWhitelist(Whitelist whitelist) throws Exception;

    /**
     * This method deletes an existing sap configuration from the local database. Referenced table entries are also deleted.
     * If the sap configuration has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param config the sap configuration to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void deleteSapConfig(SapConfiguration config) throws Exception;

    // TODO: add methods for archiving old sap queries

    // ============================================
    //          U S E R   A C C O U N T S
    // ============================================

    /**
     * This method adds a new database user with rights according to the given role.
     *
     * @param username the name of the new database user
     * @param password the password of the new database user
     * @param role the role of the new database user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void createDatabaseUser(String username, String password, DbUserRole role) throws Exception;

    /**
     * This method changes the role of an existing user.
     *
     * @param username the name of an existing database user
     * @param role the new role of the user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void changeUserRole(String username, DbUserRole role) throws Exception;

    /**
     * This method deletes an existing database user.
     *
     * @param username the name of the user to delete
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void deleteDatabaseUser(String username) throws Exception;

    // ============================================
    //                  L O G I N
    // ============================================

    /**
     * This method switches the logged in user. (it also works for first login)
     *
     * @param username the name of the new user
     * @param password the password of the new user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    boolean switchUser(String username, String password) throws Exception;

}
