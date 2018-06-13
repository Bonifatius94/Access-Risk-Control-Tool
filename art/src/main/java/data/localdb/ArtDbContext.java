package data.localdb;

import data.entities.AccessCondition;
import data.entities.AccessConditionType;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.DbUser;
import data.entities.DbUserRole;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ArtDbContext extends H2ContextBase implements IArtDbContext {

    // ===================================
    //             CONSTRUCTOR
    // ===================================

    public ArtDbContext(String username, String password) {
        super(getDefaultDatabaseFilePath(), username, password);
    }

    public ArtDbContext(String filePath, String username, String password) {
        super(filePath, username, password);
    }

    private static String getDefaultDatabaseFilePath() {

        String currentExeFolder = System.getProperty("user.dir");
        return Paths.get(currentExeFolder, "foo.h2").toAbsolutePath().toString();
    }

    // ===================================
    //         INIT DATA ENTITIES
    // ===================================

    @Override
    protected List<Class> getAnnotatedClasses() {

        List<Class> list = new ArrayList<>();

        // configuration
        list.add(Configuration.class);

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

        return list;
    }

    @Override
    protected void setAdditionalProperties(org.hibernate.cfg.Configuration config) {

        super.setAdditionalProperties(config);

        // apply scripts that are executed after a new database file has been created by hibernate
        config.setProperty("hibernate.hbm2ddl.import_files", "scripts/create_views.sql, scripts/create_roles.sql");
    }

    // ============================================
    //        H I B E R N A T E    L O G I C
    // ============================================

    // ============================================
    //                 C R E A T E
    // ============================================

    /**
     * This method writes the results of an already executed sap query to the local database. All referenced table entries are also created.
     *
     * @param query the query to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createSapQuery(CriticalAccessQuery query) throws Exception {

        query.adjustReferences();
        query.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(query);
    }

    /**
     * This method creates a new configuration in the local database. No referenced table entries are created.
     *
     * @param config the configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createConfig(Configuration config) throws Exception {

        config.adjustReferences();
        config.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(config);
    }

    /**
     * This method creates a new access pattern in the local database. All referenced table entries are also created.
     *
     * @param pattern the access pattern to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createPattern(AccessPattern pattern) throws Exception {

        pattern.adjustReferences();
        pattern.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(pattern);
    }

    /**
     * This method creates a new whitelist in the local database. All referenced table entries are also created.
     *
     * @param whitelist the whitelist to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createWhitelist(Whitelist whitelist) throws Exception {

        whitelist.adjustReferences();
        whitelist.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(whitelist);
    }

    /**
     * This method creates a new sap configuration in the local database. All referenced table entries are also created.
     *
     * @param config the sap configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createSapConfig(SapConfiguration config) throws Exception {

        config.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(config);
    }

    // ============================================
    //                   R E A D
    // ============================================

    // TODO: add filter options (e.g. by time: today, last week, last month, last year, all)

    /**
     * This method selects all already executed sap queries from the local database.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of already executed sap queries
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<CriticalAccessQuery> getSapQueries(boolean includeArchived) throws Exception {

        return queryDataset("FROM CriticalAccessQuery" + (includeArchived ? "" : " WHERE IsArchived = 0"));
    }

    /**
     * This method selects all configurations from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<Configuration> getConfigs(boolean includeArchived) throws Exception {

        return queryDataset("FROM Configuration" + (includeArchived ? "" : " WHERE IsArchived = 0"));
    }

    /**
     * This method selects all access patterns from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all access patterns
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<AccessPattern> getPatterns(boolean includeArchived) throws Exception {

        return queryDataset("FROM AccessPattern" + (includeArchived ? "" : " WHERE IsArchived = 0"));
    }

    /**
     * This method selects all whitelists from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all whitelists
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<Whitelist> getWhitelists(boolean includeArchived) throws Exception {

        return queryDataset("FROM Whitelist" + (includeArchived ? "" : " WHERE IsArchived = 0"));
    }

    /**
     * This method selects all sap configurations from the local database that are not archived with history flag.
     *
     * @param includeArchived determines whether archived records are also loaded
     * @return a list of all sap configurations
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public List<SapConfiguration> getSapConfigs(boolean includeArchived) throws Exception {

        return queryDataset("FROM SapConfiguration" + (includeArchived ? "" : " WHERE IsArchived = 0"));
    }

    /**
     * This method selects all user names of existing local database accounts and their privileges.
     *
     * @return a list of local database users
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DbUser> getDatabaseUsers() throws Exception {

        List<DbUser> users;

        try (Session session = getSessionFactory().openSession()) {

            List<Object[]> results = session.createNativeQuery("SELECT * FROM DbUsers").getResultList();
            users = results.stream().map(x -> new DbUser((String)x[0], DbUserRole.parseRole((String)x[1]))).collect(Collectors.toList());

        } catch (Exception ex) {
            throw new Exception("Unknown error while executing local database query. (see log file for more details)");
        }

        return users;
    }

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
    @Override
    public void updateConfig(Configuration config) throws Exception {

        // TODO: test logic (especially foreign keys when archiving!!!)

        config.adjustReferences();

        // check if the config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getId().equals(config.getId()));

        if (archive) {

            // get the id of the original config
            Integer originalId = config.getId();
            Configuration original = getConfigs(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

            // set archived flag to original and update it
            original.setArchived(true);
            updateRecord(original);

            // request new id for config to update
            config.setId(null);
            insertRecord(config);

        } else {
            updateRecord(config);
        }
    }

    /**
     * This method updates all properties referenced in the access pattern.
     *
     * @param pattern the access pattern to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updatePattern(AccessPattern pattern) throws Exception {

        // TODO: test logic (especially foreign keys when archiving!!!)

        pattern.adjustReferences();

        // check if the pattern has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getPatterns().stream().anyMatch(y -> y.getId().equals(pattern.getId())));

        if (archive) {

            // get the id of the original pattern
            Integer originalId = pattern.getId();
            AccessPattern original = getPatterns(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

            // set archived flag to original and update it
            original.setArchived(true);
            updateRecord(original);

            // clone new pattern (without configs referenced)
            // public AccessPattern(String usecaseId, String description, List<AccessCondition> conditions, ConditionLinkage linkage) {

            // TODO: make this work

            /*AccessPattern newPattern = new AccessPattern(pattern.getUsecaseId(), pattern.getDescription(), new ArrayList<>(), pattern.getLinkage());
            pattern.getConditions().stream().map(x -> {

                AccessCondition condition;

                if (x.getType() == AccessConditionType.Profile) {
                    AccessProfileCondition profileCondition = new AccessProfileCondition(x.getProfileCondition().getProfile());
                    condition = new AccessCondition(newPattern, profileCondition);
                } else {

                }

                x.setId(null);

                if (x.getType() == AccessConditionType.Profile) {
                    x.getProfileCondition().setId(null);
                } else {
                    x.getPatternCondition().setId(null);
                }
            });*/

            /*// clone configs referencing the pattern
            Set<Configuration> newConfigs = configs.stream().map(x -> {

                Configuration copy = new Configuration();

                copy.setName(x.getName());
                copy.setDescription(x.getDescription());
                copy.setWhitelist(x.getWhitelist());


                copy.getPatterns().addAll();
                copy.getPatterns().remove();

                return copy;
            }).collect(Collectors.toSet());*/

            // request new ids for pattern to update
            pattern.setId(null);

            pattern.getConditions().forEach(x -> {
                x.setId(null);

                if (x.getType() == AccessConditionType.Profile) {
                    x.getProfileCondition().setId(null);
                } else {
                    x.getPatternCondition().setId(null);
                }
            });

            // insert new pattern into database
            insertRecord(pattern);

            // create new configs referencing the new pattern


        } else {
            updateRecord(pattern);
        }
    }

    /**
     * This method updates all properties referenced in the whitelist.
     *
     * @param whitelist the whitelist to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateWhitelist(Whitelist whitelist) throws Exception {

        // TODO: test logic (especially foreign keys when archiving!!!)

        whitelist.adjustReferences();

        // check if the whitelist has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getWhitelist().getId().equals(whitelist.getId()));

        if (archive) {

            // get the id of the original whitelist
            Integer originalId = whitelist.getId();
            Whitelist original = getWhitelists(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

            // set archived flag to original and update it
            original.setArchived(true);
            updateRecord(original);

            // request new id for pattern to update
            whitelist.setId(null);
        }

        updateRecord(whitelist);
    }

    /**
     * This method updates all properties referenced in the config.
     *
     * @param config the config to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateSapConfig(SapConfiguration config) throws Exception {

        // TODO: test logic (especially foreign keys when archiving!!!)

        // check if the sap config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getSapConfig().getId().equals(config.getId()));

        if (archive) {

            // get the id of the original whitelist
            Integer originalId = config.getId();
            SapConfiguration original = getSapConfigs(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

            // set archived flag to original and update it
            original.setArchived(true);
            updateRecord(original);

            // request new id for pattern to update
            config.setId(null);
        }

        updateRecord(config);
    }

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
    @Override
    public void deleteConfig(Configuration config) throws Exception {

        config.adjustReferences();

        // check if the config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getId().equals(config.getId()));

        if (archive) {

            config.setArchived(true);
            updateRecord(config);

        } else {

            deleteRecord(config);
        }
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

        pattern.adjustReferences();

        // check if the pattern has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getPatterns().stream().anyMatch(y -> y.getId().equals(pattern.getId())));

        if (archive) {

            pattern.setArchived(true);
            updateRecord(pattern);

        } else {

            // remove pattern references on configs and update configs to remove entries on mapping table
            pattern.getConfigurations().stream().filter(x -> x.getPatterns().contains(pattern))
                .forEach(x -> {
                    x.getPatterns().remove(pattern);
                    updateRecord(x);
                });

            // delete pattern
            deleteRecord(pattern);
        }
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

        whitelist.adjustReferences();

        // check if the whitelist has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getWhitelist().getId().equals(whitelist.getId()));

        if (archive) {

            whitelist.setArchived(true);
            updateRecord(whitelist);

        } else {

            // remove whitelist references on configs and update configs to remove entries on mapping table
            getConfigs(true).stream().filter(x -> x.getWhitelist().getId().equals(whitelist.getId()))
                .forEach(x -> {
                    x.setWhitelist(null);
                    updateRecord(x);
                });

            // delete whitelist
            deleteRecord(whitelist);
        }
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

        // check if the sap config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getSapConfig().getId().equals(config.getId()));

        if (archive) {

            config.setArchived(true);
            updateRecord(config);

        } else {

            // delete the sap config references in all queries
            getSapQueries(true).stream().filter(x -> x.getSapConfig().getId().equals(config.getId()))
                .forEach(x -> {
                    x.setSapConfig(null);
                    updateRecord(x);
                });

            deleteRecord(config);
        }
    }

    // ============================================
    //          U S E R   A C C O U N T S
    // ============================================

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

        Transaction transaction = null;

        try (Session session = getSessionFactory().openSession()) {

            // avoid sql injection with username
            if (username.contains(" ")) {
                // TODO: implement this as custom exception
                throw new Exception("Invalid username! No whitespaces allowed!");
            }

            transaction = session.beginTransaction();

            // create a new database account
            String sql = "CREATE USER " + username + " PASSWORD :password " + ((role == DbUserRole.Admin) ? "ADMIN" : "");
            session.createNativeQuery(sql).setParameter("password", password).executeUpdate();

            // grant privileges to account accordingly
            session.createNativeQuery("GRANT " + role.toString() + " TO " + username).executeUpdate();

            session.flush();
            transaction.commit();

        } catch (Exception ex) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw ex;
        }
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

        // get original database user and role
        DbUser original =
            getDatabaseUsers()
            .stream().filter(x -> x.getUsername().toUpperCase().equals(username.toUpperCase()))
            .findFirst().orElse(null);

        if (original != null) {

            Transaction transaction = null;

            try (Session session = getSessionFactory().openSession()) {

                transaction = session.beginTransaction();

                // remove old privileges from database account
                session.createNativeQuery("REVOKE " + original.getRole().toString() + " FROM " + username).executeUpdate();

                // grant new privileges to database account
                session.createNativeQuery("GRANT " + role.toString() + " TO " + username).executeUpdate();

                session.flush();
                transaction.commit();

            } catch (Exception ex) {

                if (transaction != null) {
                    transaction.rollback();
                }

                throw ex;
            }

        } else {
            throw new Exception("User " + username + " does not exist.");
        }
    }

    /**
     * This method changes the password of an existing user.
     *
     * @param username the name of an existing database user
     * @param password the new password of the user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void changePassword(String username, String password) throws Exception {

        Transaction transaction = null;

        try (Session session = getSessionFactory().openSession()) {

            // avoid sql injection with username
            if (username.contains(" ")) {
                // TODO: implement this as custom exception
                throw new Exception("Invalid username! No whitespaces allowed!");
            }

            transaction = session.beginTransaction();

            // change password for database account
            session.createNativeQuery("ALTER USER " + username + " SET PASSWORD :password").setParameter("password", password).executeUpdate();

            session.flush();
            transaction.commit();

        } catch (Exception ex) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw ex;
        }
    }

    /**
     * This method deletes an existing database user.
     *
     * @param username the name of the user to delete
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void deleteDatabaseUser(String username) throws Exception {

        Transaction transaction = null;

        try (Session session = getSessionFactory().openSession()) {

            // avoid sql injection with username
            if (username.contains(" ")) {
                // TODO: implement this as custom exception
                throw new Exception("Invalid username! No whitespaces allowed!");
            }

            transaction = session.beginTransaction();

            // delete database account
            session.createNativeQuery("DROP USER " + username).executeUpdate();

            session.flush();
            transaction.commit();

        } catch (Exception ex) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw ex;
        }
    }

    /**
     * This method switches the logged in user. (it also works for first login)
     *
     * @param username the name of the new user
     * @param password the password of the new user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    @SuppressWarnings("unchecked")
    @Deprecated
    public void switchUser(String username, String password) throws Exception {

        // TODO: remove this operation because it is obsolete. close application and login with other user after restart instead.
        throw new Exception("Operation is obsolete. Please do not use it!");

        //try (Session session = sessionFactory.openSession()) {
        //
        //    List<DbUser> registeredUsers = getDatabaseUsers();
        //
        //    if (registeredUsers.stream().anyMatch(x -> x.getUsername().toUpperCase().equals(username.toUpperCase()))) {
        //        super.changeUser(username, password);
        //    } else {
        //        throw new ArtException(ArtException.ErrorCode.UnregisteredLocalDatabaseUser, null);
        //    }
        //
        //} catch (Exception ex) {
        //    throw new ArtException(ArtException.ErrorCode.WrongLocalDatabaseLoginCredentials, null);
        //}
    }

}
