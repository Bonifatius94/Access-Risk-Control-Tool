package data.localdb;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.DbUser;
import data.entities.DbUserRole;
import data.entities.SapConfiguration;
import data.entities.Whitelist;

import java.time.ZonedDateTime;
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
     * This method selects all already executed sap queries from the local database.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of already executed sap queries
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<CriticalAccessQuery> getSapQueries(boolean includeArchived) throws Exception;

    /**
     * This method selects all configurations from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<Configuration> getConfigs(boolean includeArchived) throws Exception;

    /**
     * This method selects all access patterns from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all access patterns
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<AccessPattern> getPatterns(boolean includeArchived) throws Exception;

    /**
     * This method selects all whitelists from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all whitelists
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<Whitelist> getWhitelists(boolean includeArchived) throws Exception;

    /**
     * This method selects all sap configurations from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all sap configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<SapConfiguration> getSapConfigs(boolean includeArchived) throws Exception;

    /**
     * This method selects all user names of existing local database accounts and their privileges.
     *
     * @return a list of local database users
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    List<DbUser> getDatabaseUsers() throws Exception;

    // ============================================
    //               F I L T E R S
    // ============================================

    /**
     * This method applies the given filter options to an access pattern query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard the wildcard string that is searched in several text attributes of whitelists
     * @param start the lower limit of the whitelist creation timestamp to be filtered
     * @param end the upper limit of the whitelist creation timestamp to be filtered
     * @param limit the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    List<AccessPattern> getFilteredPatterns(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception;

    /**
     * This method applies the given filter options to a whitelist query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard the wildcard string that is searched in several text attributes of whitelists
     * @param start the lower limit of the whitelist creation timestamp to be filtered
     * @param end the upper limit of the whitelist creation timestamp to be filtered
     * @param limit the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    List<Whitelist> getFilteredWhitelists(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception;

    /**
     * This method applies the given filter options to a sap configs query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard the wildcard string that is searched in several text attributes of whitelists
     * @param start the lower limit of the whitelist creation timestamp to be filtered
     * @param end the upper limit of the whitelist creation timestamp to be filtered
     * @param limit the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    List<SapConfiguration> getFilteredSapConfigs(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception;

    /**
     * This method applies the given filter options to a configs query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard the wildcard string that is searched in several text attributes of whitelists
     * @param start the lower limit of the whitelist creation timestamp to be filtered
     * @param end the upper limit of the whitelist creation timestamp to be filtered
     * @param limit the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    List<Configuration> getFilteredConfigs(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception;

    /**
     * This method applies the given filter options to a sap query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard the wildcard string that is searched in several text attributes of whitelists
     * @param start the lower limit of the whitelist creation timestamp to be filtered
     * @param end the upper limit of the whitelist creation timestamp to be filtered
     * @param limit the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    List<CriticalAccessQuery> getFilteredCriticalAccessQueries(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception;

    /**
     * This method selects all already executed sap queries from the local database. The filters are applied as in the getFilteredCriticalAccessQueries() method.
     *
     * @param query the query to be related to
     * @param includeArchived determines whether archived records are also loaded
     * @param wildcard        the wildcard string that is searched in several text attributes of whitelists
     * @param start           the lower limit of the whitelist creation timestamp to be filtered
     * @param end             the upper limit of the whitelist creation timestamp to be filtered
     * @param limit           the limit of records returned
     * @return a list of already executed sap queries
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    public List<CriticalAccessQuery> getRelatedFilteredCriticalAccessQueries(
        CriticalAccessQuery query, boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception;

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

    // ============================================
    //          U S E R   A C C O U N T S
    // ============================================

    /**
     * This method adds a new database user with rights according to the given role.
     *
     * @param user the account data of the new database user
     * @param password the password of the new database user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void createDatabaseUser(DbUser user, String password) throws Exception;

    /**
     * This method changes the roles of an existing user.
     *
     * @param user the database user to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void updateUserRoles(DbUser user) throws Exception;

    /**
     * This method changes the password of an existing user.
     *
     * @param username the name of an existing database user
     * @param password the new password of the user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void changePassword(String username, String password) throws Exception;

    /**
     * This method deletes an existing database user.
     *
     * @param username the name of the user to delete
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    void deleteDatabaseUser(String username) throws Exception;

    /**
     * This method gets the current logged-in database user.
     *
     * @return the current logged-in user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    DbUser getCurrentUser() throws Exception;

    /**
     * This method sets the first login flag of the current user.
     *
     * @param user the current logged-in user
     * @param flag the first login flag to be set
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    public void setFirstLoginOfCurrentUser(DbUser user, boolean flag) throws Exception;

}
