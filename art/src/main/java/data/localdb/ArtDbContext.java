package data.localdb;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.Configuration;
import data.entities.ConfigurationXAccessPatternMap;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.DbUserRole;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.util.ArrayList;
import java.util.List;

public class ArtDbContext extends H2ContextBase implements IArtDbContext {

    public ArtDbContext(String username, String password) {
        super(username, password);
    }

    @Override
    protected List<Class> getAnnotatedClasses() {

        List<Class> list = new ArrayList<>();

        // configuration
        list.add(Configuration.class);
        list.add(ConfigurationXAccessPatternMap.class);

        // access pattern
        list.add(AccessPattern.class);
        list.add(AccessCondition.class);
        list.add(AccessProfileCondition.class);
        list.add(AccessPatternCondition.class);
        list.add(AccessPatternConditionProperty.class);

        // whitelist
        list.add(Whitelist.class);
        list.add(WhitelistEntry.class);

        // sap query
        list.add(CriticalAccessQuery.class);
        list.add(CriticalAccessEntry.class);

        // sap config
        list.add(SapConfiguration.class);

        // TODO: only add this entity during query, not here (see official hibernate guide '17.3 entity queries')
        // database user accounts
        //list.add(DbUser.class);

        return list;
    }

    /**
     * This method writes the results of an already executed sap query to the local database. All referenced table entries are also created.
     *
     * @param query the query to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createSapQuery(CriticalAccessQuery query) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method creates a new configuration in the local database. No referenced table entries are created.
     *
     * @param config the configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createConfig(Configuration config) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method creates a new access pattern in the local database. All referenced table entries are also created.
     *
     * @param pattern the access pattern to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createPattern(AccessPattern pattern) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method creates a new whitelist in the local database. All referenced table entries are also created.
     *
     * @param whitelist the whitelist to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createWhitelist(Whitelist whitelist) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method creates a new sap configuration in the local database. All referenced table entries are also created.
     *
     * @param config the sap configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createSapConfig(SapConfiguration config) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method selects all already executed ART queries from the local database.
     * TODO: add filter option (e.g. by time: today, last week, last month, last year, all)
     *
     * @return a list of already executed ART queries
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<CriticalAccessQuery> getArtQueries() throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method selects all configurations from the local database that are not archived with history flag.
     *
     * @return a list of all configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<Configuration> getConfigs() throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method selects all access patterns from the local database that are not archived with history flag.
     *
     * @return a list of all access patterns
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<AccessPattern> getPatterns() throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method selects all whitelists from the local database that are not archived with history flag.
     *
     * @return a list of all whitelists
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<Whitelist> getWhitelists() throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method selects all sap configurations from the local database that are not archived with history flag.
     *
     * @return a list of all sap configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<SapConfiguration> getSapConfigs() throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method selects all user names of existing local database accounts and their privileges.
     *
     * @return a list of local database users
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<String> getDatabaseUsers() throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method updates all properties referenced in the config.
     * If the config has already been used in sap queries, the history flag is set and the changes are applied to a new
     *
     * @param config the config to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateConfig(Configuration config) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method updates all properties referenced in the access pattern.
     *
     * @param pattern the access pattern to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updatePattern(AccessPattern pattern) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method updates all properties referenced in the whitelist.
     *
     * @param whitelist the whitelist to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateWhitelist(Whitelist whitelist) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method updates all properties referenced in the config.
     *
     * @param config the config to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateSapConfig(SapConfiguration config) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method deletes an existing configuration from the local database. Referenced table entries are not deleted.
     * If the config has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param config the configuration to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void deleteConfig(Configuration config) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method deletes an existing access pattern from the local database. Referenced table entries are also deleted.
     * If the pattern has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param pattern the access pattern to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void deletePattern(AccessPattern pattern) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method deletes an existing whitelist from the local database. Referenced table entries are also deleted.
     * If the whitelist has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param whitelist the whitelist to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void deleteWhitelist(Whitelist whitelist) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method deletes an existing sap configuration from the local database. Referenced table entries are also deleted.
     * If the sap configuration has already been used in sap queries, the history flag is set instead of deleting.
     *
     * @param config the sap configuration to be deleted
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void deleteSapConfig(SapConfiguration config) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method adds a new database user with rights according to the given role.
     *
     * @param username the name of the new database user
     * @param password the password of the new database user
     * @param role     the role of the new database user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createDatabaseUser(String username, String password, DbUserRole role) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method changes the role of an existing user.
     *
     * @param username the name of an existing database user
     * @param role     the new role of the user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void changeUserRole(String username, DbUserRole role) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method deletes an existing database user.
     *
     * @param username the name of the user to delete
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void deleteDatabaseUser(String username) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

    /**
     * This method switches the logged in user. (it also works for first login)
     *
     * @param username the name of the new user
     * @param password the password of the new user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void switchUser(String username, String password) throws Exception {
        // TODO: implement logic
        throw new Exception("Logic has not been implemented yet");
    }

}
