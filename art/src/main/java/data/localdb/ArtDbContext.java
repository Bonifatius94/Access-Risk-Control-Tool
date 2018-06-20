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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import tools.tracing.TraceOut;

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
        return Paths.get(currentExeFolder, "art.h2").toAbsolutePath().toString();
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

        // configure hibernate logging level
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        // TODO: remove all 'hibernate.hbm2ddl' settings in production builds
    }

    // ============================================
    //       H I B E R N A T E    L O G I C
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

        TraceOut.enter();

        query.adjustReferences();
        query.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(query);

        TraceOut.leave();
    }

    /**
     * This method creates a new configuration in the local database. No referenced table entries are created.
     *
     * @param config the configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createConfig(Configuration config) throws Exception {

        TraceOut.enter();

        config.adjustReferences();
        config.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(config);

        TraceOut.leave();
    }

    /**
     * This method creates a new access pattern in the local database. All referenced table entries are also created.
     *
     * @param pattern the access pattern to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createPattern(AccessPattern pattern) throws Exception {

        TraceOut.enter();

        pattern.adjustReferences();
        pattern.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(pattern);

        TraceOut.leave();
    }

    /**
     * This method creates a new whitelist in the local database. All referenced table entries are also created.
     *
     * @param whitelist the whitelist to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createWhitelist(Whitelist whitelist) throws Exception {

        TraceOut.enter();

        whitelist.adjustReferences();
        whitelist.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(whitelist);

        TraceOut.leave();
    }

    /**
     * This method creates a new sap configuration in the local database. All referenced table entries are also created.
     *
     * @param config the sap configuration to be inserted into local database
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createSapConfig(SapConfiguration config) throws Exception {

        TraceOut.enter();

        config.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(config);

        TraceOut.leave();
    }

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
    @Override
    public List<CriticalAccessQuery> getSapQueries(boolean includeArchived) throws Exception {

        TraceOut.enter();

        List<CriticalAccessQuery> queries = queryDataset("FROM CriticalAccessQuery" + (includeArchived ? "" : " WHERE IsArchived = 0"));

        TraceOut.leave();
        return queries;
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

        TraceOut.enter();

        List<Configuration> configs =  queryDataset("FROM Configuration" + (includeArchived ? "" : " WHERE IsArchived = 0"));

        TraceOut.leave();
        return configs;
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

        TraceOut.enter();

        List<AccessPattern> patterns = queryDataset("FROM AccessPattern" + (includeArchived ? "" : " WHERE IsArchived = 0"));

        TraceOut.leave();
        return patterns;
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

        TraceOut.enter();

        List<Whitelist> whitelists = queryDataset("FROM Whitelist" + (includeArchived ? "" : " WHERE IsArchived = 0"));

        TraceOut.leave();
        return whitelists;
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

        TraceOut.enter();

        List<SapConfiguration> configs = queryDataset("FROM SapConfiguration" + (includeArchived ? "" : " WHERE IsArchived = 0"));

        TraceOut.leave();
        return configs;
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

        TraceOut.enter();

        List<DbUser> users;

        try (Session session = getSessionFactory().openSession()) {

            List<Object[]> results = session.createNativeQuery("SELECT * FROM DbUsers").getResultList();

            users = results.stream().map(x -> {

                String username = (String)x[0];
                Set<DbUserRole> roles = Arrays.stream(((String) x[1]).split(",")).map(y -> DbUserRole.parseRole(y)).collect(Collectors.toSet());

                return new DbUser(username, roles);

            }).collect(Collectors.toList());

        } catch (Exception ex) {
            throw new Exception("Unknown error while executing local database query. (see log file for more details)");
        }

        TraceOut.leave();
        return users;
    }

    // ============================================
    //               F I L T E R S
    // ============================================

    /**
     * This method applies the given filter options to an access pattern query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard        the wildcard string that is searched in several text attributes of whitelists
     * @param start           the lower limit of the whitelist creation timestamp to be filtered
     * @param end             the upper limit of the whitelist creation timestamp to be filtered
     * @param limit           the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AccessPattern> getFilteredPatterns(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception {

        TraceOut.enter();

        List<AccessPattern> results;

        try (Session session = openSession()) {

            // prepare query
            String sql = getFilteredPatternsQuerySql(includeArchived, wildcard, start, end, limit);
            NativeQuery query = session.createNativeQuery(sql);

            // set parameters
            if (wildcard != null && !wildcard.isEmpty()) {
                query.setParameter("wildcard", "%" + wildcard + "%");
            }

            if (start != null) {
                query.setParameter("start", start.toLocalDate());
            }

            if (end != null) {
                query.setParameter("end", end.toLocalDate());
            }

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            // execute query
            query.addEntity(AccessPattern.class);
            results = query.list();

        } catch (Exception ex) {
            // TODO: implement custom exception
            throw ex;
        }

        TraceOut.leave();
        return results;
    }

    private String getFilteredPatternsQuerySql(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) {

        String sql =
              "SELECT DISTINCT Pattern.* "
            + "FROM AccessPatterns AS Pattern "
            + "INNER JOIN AccessConditions AS Condition ON Condition.PatternId = Pattern.id "
            + "LEFT OUTER JOIN AccessPatternConditions AS PatternCondition ON PatternCondition.Condition_Id = Condition.id "
            + "LEFT OUTER JOIN AccessPatternConditionProperties AS PatternConditionProperty ON PatternConditionProperty.ConditionId = Condition.id "
            + "LEFT OUTER JOIN AccessProfileConditions AS ProfileCondition ON ProfileCondition.Condition_Id = Condition.id ";

        List<String> conditions = new ArrayList<>();

        if (!includeArchived) {
            conditions.add("Pattern.isArchived = 0");
        }

        if (wildcard != null && !wildcard.isEmpty()) {

            conditions.add("LOWER(Pattern.usecaseId) LIKE LOWER(:wildcard) OR LOWER(Pattern.description) LIKE LOWER(:wildcard) "
                + "OR LOWER(PatternConditionProperty.authObject) LIKE LOWER(:wildcard) OR LOWER(ProfileCondition.profile) LIKE LOWER(:wildcard)");
        }

        if (start != null) {
            conditions.add("Pattern.createdAt >= :start");
        }

        if (end != null) {
            conditions.add("Pattern.createdAt <= :end");
        }

        if (conditions.size() > 0) {
            sql += "WHERE " + conditions.stream().map(x -> "(" + x + ")").reduce((x, y) -> x + " AND " + y).get() + " ";
        }

        sql += "ORDER BY Pattern.usecaseId";

        return sql;
    }

    /**
     * This method applies the given filter options to a whitelist query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard        the wildcard string that is searched in several text attributes of whitelists
     * @param start           the lower limit of the whitelist creation timestamp to be filtered
     * @param end             the upper limit of the whitelist creation timestamp to be filtered
     * @param limit           the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Whitelist> getFilteredWhitelists(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception {

        TraceOut.enter();

        List<Whitelist> results;

        try (Session session = openSession()) {

            // prepare query
            String sql = getFilteredWhitelistQuerySql(includeArchived, wildcard, start, end, limit);
            NativeQuery query = session.createNativeQuery(sql);

            // set parameters
            if (wildcard != null && !wildcard.isEmpty()) {
                query.setParameter("wildcard", "%" + wildcard + "%");
            }

            if (start != null) {
                query.setParameter("start", start.toLocalDate());
            }

            if (end != null) {
                query.setParameter("end", end.toLocalDate());
            }

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            // execute query
            query.addEntity(Whitelist.class);
            results = query.list();

        } catch (Exception ex) {
            // TODO: implement custom exception
            throw ex;
        }

        TraceOut.leave();
        return results;
    }

    private String getFilteredWhitelistQuerySql(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) {

        String sql =
            "SELECT DISTINCT Whitelist.* "
                + "FROM Whitelists AS Whitelist "
                + "INNER JOIN WhitelistEntries AS Entries ON Entries.WhitelistId = Whitelist.id ";

        List<String> conditions = new ArrayList<>();

        if (!includeArchived) {
            conditions.add("Whitelist.isArchived = 0");
        }

        if (wildcard != null && !wildcard.isEmpty()) {

            conditions.add("LOWER(Whitelist.name) LIKE LOWER(:wildcard) OR LOWER(Whitelist.description) LIKE LOWER(:wildcard) "
                + "OR LOWER(Entries.usecaseId) LIKE LOWER(:wildcard) OR LOWER(Entries.username) LIKE LOWER(:wildcard)");
        }

        if (start != null) {
            conditions.add("Whitelist.createdAt >= :start");
        }

        if (end != null) {
            conditions.add("Whitelist.createdAt <= :end");
        }

        if (conditions.size() > 0) {
            sql += "WHERE " + conditions.stream().map(x -> "(" + x + ")").reduce((x, y) -> x + " AND " + y).get() + " ";
        }

        sql += "ORDER BY Whitelist.name";

        return sql;
    }

    /**
     * This method applies the given filter options to a sap configs query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard        the wildcard string that is searched in several text attributes of whitelists
     * @param start           the lower limit of the whitelist creation timestamp to be filtered
     * @param end             the upper limit of the whitelist creation timestamp to be filtered
     * @param limit           the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<SapConfiguration> getFilteredSapConfigs(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception {

        TraceOut.enter();

        List<SapConfiguration> results;

        try (Session session = openSession()) {

            // prepare query
            String sql = getFilteredSapConfigQuerySql(includeArchived, wildcard, start, end, limit);
            NativeQuery query = session.createNativeQuery(sql);

            // set parameters
            if (wildcard != null && !wildcard.isEmpty()) {
                query.setParameter("wildcard", "%" + wildcard + "%");
            }

            if (start != null) {
                query.setParameter("start", start.toLocalDate());
            }

            if (end != null) {
                query.setParameter("end", end.toLocalDate());
            }

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            // execute query
            query.addEntity(SapConfiguration.class);
            results = query.list();

        } catch (Exception ex) {
            // TODO: implement custom exception
            throw ex;
        }

        TraceOut.leave();
        return results;
    }

    private String getFilteredSapConfigQuerySql(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) {

        String sql = "SELECT * FROM SapConfigurations AS SapConfig ";
        List<String> conditions = new ArrayList<>();

        if (!includeArchived) {
            conditions.add("SapConfig.isArchived = 0");
        }

        if (wildcard != null && !wildcard.isEmpty()) {
            conditions.add("LOWER(SapConfig.serverDestination) LIKE LOWER(:wildcard) OR LOWER(SapConfig.description) LIKE LOWER(:wildcard)");
        }

        if (start != null) {
            conditions.add("SapConfig.createdAt >= :start");
        }

        if (end != null) {
            conditions.add("SapConfig.createdAt <= :end");
        }

        if (conditions.size() > 0) {
            sql += "WHERE " + conditions.stream().map(x -> "(" + x + ")").reduce((x, y) -> x + " AND " + y).get() + " ";
        }

        sql += "ORDER BY SapConfig.serverDestination";

        return sql;
    }

    /**
     * This method applies the given filter options to a configs query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard        the wildcard string that is searched in several text attributes of whitelists
     * @param start           the lower limit of the whitelist creation timestamp to be filtered
     * @param end             the upper limit of the whitelist creation timestamp to be filtered
     * @param limit           the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Configuration> getFilteredConfigs(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception {

        TraceOut.enter();

        List<Configuration> results;

        try (Session session = openSession()) {

            // prepare query
            String sql = getFilteredConfigQuerySql(includeArchived, wildcard, start, end, limit);
            NativeQuery query = session.createNativeQuery(sql);

            // set parameters
            if (wildcard != null && !wildcard.isEmpty()) {
                query.setParameter("wildcard", "%" + wildcard + "%");
            }

            if (start != null) {
                query.setParameter("start", start.toLocalDate());
            }

            if (end != null) {
                query.setParameter("end", end.toLocalDate());
            }

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            // execute query
            query.addEntity(Configuration.class);
            results = query.list();

        } catch (Exception ex) {
            // TODO: implement custom exception
            throw ex;
        }

        TraceOut.leave();
        return results;
    }

    private String getFilteredConfigQuerySql(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) {

        String sql =
            "SELECT DISTINCT Config.* "
                + "FROM Configurations AS Config "
                + "LEFT OUTER JOIN Whitelists AS Whitelist ON Whitelist.id = Config.WhitelistId "
                + "LEFT OUTER JOIN nm_Configuration_AccessPattern AS Map ON Map.ConfigId = Config.id "
                + "LEFT OUTER JOIN AccessPatterns AS Pattern ON Pattern.id = Map.AccessPatternId ";

        List<String> conditions = new ArrayList<>();

        if (!includeArchived) {
            conditions.add("Config.isArchived = 0");
        }

        if (wildcard != null && !wildcard.isEmpty()) {

            conditions.add("LOWER(Config.name) LIKE LOWER(:wildcard) OR LOWER(Config.description) LIKE LOWER(:wildcard) "
                + "OR LOWER(Whitelist.name) LIKE LOWER(:wildcard) OR LOWER(Pattern.usecaseId) LIKE LOWER(:wildcard)");
        }

        if (start != null) {
            conditions.add("Config.createdAt >= :start");
        }

        if (end != null) {
            conditions.add("Config.createdAt <= :end");
        }

        if (conditions.size() > 0) {
            sql += "WHERE " + conditions.stream().map(x -> "(" + x + ")").reduce((x, y) -> x + " AND " + y).get() + " ";
        }

        sql += "ORDER BY Config.name";

        return sql;
    }

    /**
     * This method applies the given filter options to a sap query. Include archived / wildcard / datetime range / limit are applied if not null (with AND linkage).
     *
     * @param includeArchived a flag that indicates whether the archived whitelists are also included
     * @param wildcard        the wildcard string that is searched in several text attributes of whitelists
     * @param start           the lower limit of the whitelist creation timestamp to be filtered
     * @param end             the upper limit of the whitelist creation timestamp to be filtered
     * @param limit           the limit of records returned
     * @return a list of whitelist matching the given filter options
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CriticalAccessQuery> getFilteredCriticalAccessQueries(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) throws Exception {

        TraceOut.enter();
        List<CriticalAccessQuery> results;

        try (Session session = openSession()) {

            // prepare query
            String sql = getFilteredCriticalAccessQuerySql(includeArchived, wildcard, start, end, limit);
            NativeQuery query = session.createNativeQuery(sql);

            // set parameters
            if (wildcard != null && !wildcard.isEmpty()) {
                query.setParameter("wildcard", "%" + wildcard + "%");
            }

            if (start != null) {
                query.setParameter("start", start.toLocalDate());
            }

            if (end != null) {
                query.setParameter("end", end.toLocalDate());
            }

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            // execute query
            query.addEntity(CriticalAccessQuery.class);
            results = query.list();

        } catch (Exception ex) {
            // TODO: implement custom exception
            throw ex;
        }

        TraceOut.leave();
        return results;
    }

    private String getFilteredCriticalAccessQuerySql(boolean includeArchived, String wildcard, ZonedDateTime start, ZonedDateTime end, Integer limit) {

        String sql =
              "SELECT DISTINCT Query.* "
            + "FROM CriticalAccessQueries AS Query "
            + "INNER JOIN CriticalAccessEntries AS Entries ON Entries.queryId = Query.id "
            + "INNER JOIN AccessPatterns AS ViolatedPattern ON ViolatedPattern.id = Entries.violatedPatternId "
            + "INNER JOIN SapConfigurations AS SapConfig ON SapConfig.id = Query.sapConfigId "
            + "INNER JOIN Configurations AS Config ON Config.id = Query.configId ";

        List<String> conditions = new ArrayList<>();

        if (!includeArchived) {
            conditions.add("Query.isArchived = 0");
        }

        if (wildcard != null && !wildcard.isEmpty()) {

            conditions.add(
                  "LOWER(Config.name) LIKE LOWER(:wildcard) OR LOWER(Config.description) LIKE LOWER(:wildcard) "
                + "OR LOWER(SapConfig.serverDestination) LIKE LOWER(:wildcard) OR LOWER(SapConfig.description) LIKE LOWER(:wildcard) "
                + "OR LOWER(Entries.username) LIKE LOWER(:wildcard) OR LOWER(ViolatedPattern.usecaseId) LIKE LOWER(:wildcard)");
        }

        if (start != null) {
            conditions.add("Query.createdAt >= :start");
        }

        if (end != null) {
            conditions.add("Query.createdAt <= :end");
        }

        if (conditions.size() > 0) {
            sql += "WHERE " + conditions.stream().map(x -> "(" + x + ")").reduce((x, y) -> x + " AND " + y).get() + " ";
        }

        sql += "ORDER BY Query.createdAt DESC";

        return sql;
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

        TraceOut.enter();

        config.adjustReferences();

        // check if the config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getId().equals(config.getId()));

        if (archive) {

            archiveConfig(config);
        }

        updateRecord(config);

        TraceOut.leave();
    }

    private void archiveConfig(Configuration config) throws Exception {

        // get the id of the original config
        Integer originalId = config.getId();
        Configuration original = getConfigs(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

        // create copy of original and insert it
        Configuration archived = new Configuration(original);
        archived.setWhitelist(original.getWhitelist());
        archived.setPatterns(original.getPatterns());
        archived.setArchived(true);
        archived.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(archived);

        // reference the copy of the original in critical access queries instead of the config to update
        getSapQueries(true).stream().filter(x -> x.getConfig().getId().equals(originalId)).forEach(query -> {

            query.setConfig(archived);
            updateRecord(query);
        });
    }

    /**
     * This method updates all properties referenced in the access pattern.
     *
     * @param pattern the access pattern to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updatePattern(AccessPattern pattern) throws Exception {

        TraceOut.enter();

        pattern.adjustReferences();

        // check if the pattern has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getPatterns().stream().anyMatch(y -> y.getId().equals(pattern.getId())));

        if (archive) {

            archivePattern(pattern);
        }

        updateRecord(pattern);

        TraceOut.leave();
    }

    private void archivePattern(AccessPattern pattern) throws Exception {

        // get the id of the original pattern
        Integer originalId = pattern.getId();
        AccessPattern original = getPatterns(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

        // create copy of original and insert it
        AccessPattern archived = new AccessPattern(original);
        archived.setArchived(true);
        archived.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(archived);

        // get configs referencing the original pattern
        Set<Configuration> originalConfigs =
            getConfigs(true).stream()
                .filter(x -> x.getPatterns().stream().anyMatch(y -> x.getId().equals(y.getId())))
                .collect(Collectors.toSet());

        // get critical access queries referencing a config that references the original pattern
        Set<CriticalAccessQuery> queries =
            getSapQueries(true).stream()
                .filter(x -> originalConfigs.stream().anyMatch(y -> y.getId().equals(x.getConfig().getId())))
                .collect(Collectors.toSet());

        // copy configs where the original pattern is referenced
        originalConfigs.forEach(originalConfig -> {

            // switch pattern references
            Set<AccessPattern> patterns = originalConfig.getPatterns();
            patterns.remove(pattern);
            patterns.add(archived);

            if (originalConfig.isArchived()) {

                // reference new archived pattern
                originalConfig.setPatterns(patterns);
                updateRecord(originalConfig);

            } else {

                Configuration copy = new Configuration(originalConfig);

                // prepare and insert copy
                copy.setWhitelist(originalConfig.getWhitelist());
                copy.setPatterns(patterns);
                copy.setArchived(true);
                copy.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
                insertRecord(copy);

                // reference the new config on all critical access queries
                queries.stream().filter(x -> x.getConfig().getId().equals(originalConfig.getId())).forEach(query -> {

                    query.setConfig(copy);
                    updateRecord(query);
                });
            }
        });
    }

    /**
     * This method updates all properties referenced in the whitelist.
     *
     * @param whitelist the whitelist to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateWhitelist(Whitelist whitelist) throws Exception {

        TraceOut.enter();

        whitelist.adjustReferences();

        // check if the whitelist has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getWhitelist().getId().equals(whitelist.getId()));

        if (archive) {

            archiveWhitelist(whitelist);
        }

        updateRecord(whitelist);

        TraceOut.leave();
    }

    private void archiveWhitelist(Whitelist whitelist) throws Exception {

        // get the id of the original whitelist
        Integer originalId = whitelist.getId();
        Whitelist original = getWhitelists(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

        // create copy of original and insert it
        Whitelist archived = new Whitelist(original);
        archived.setArchived(true);
        archived.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(archived);

        // get critical access queries referencing a config that references the original pattern
        Set<CriticalAccessQuery> queries =
            getSapQueries(true).stream()
                .filter(x -> x.getConfig().getWhitelist().getId().equals(originalId))
                .collect(Collectors.toSet());

        // copy configs where the original whitelist is referenced
        getConfigs(true).stream().filter(x -> x.getWhitelist().getId().equals(originalId)).forEach(originalConfig -> {

            if (originalConfig.isArchived()) {

                // reference archived whitelist
                originalConfig.setWhitelist(archived);
                updateRecord(originalConfig);

            } else {

                Configuration copy = new Configuration(originalConfig);

                // prepare and insert copy
                copy.setWhitelist(archived);
                copy.setPatterns(originalConfig.getPatterns());
                copy.setArchived(true);
                copy.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
                insertRecord(copy);

                // reference the new config on all critical access queries
                queries.stream().filter(x -> x.getConfig().getId().equals(originalConfig.getId())).forEach(query -> {

                    query.setConfig(copy);
                    updateRecord(query);
                });
            }
        });
    }

    /**
     * This method updates all properties referenced in the config.
     *
     * @param config the config to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateSapConfig(SapConfiguration config) throws Exception {

        TraceOut.enter();

        // check if the sap config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getSapConfig().getId().equals(config.getId()));

        if (archive) {

            archiveSapConfig(config);
        }

        updateRecord(config);

        TraceOut.leave();
    }

    private void archiveSapConfig(SapConfiguration config) throws Exception {

        // get the id of the original whitelist
        Integer originalId = config.getId();
        SapConfiguration original = getSapConfigs(true).stream().filter(x -> x.getId().equals(originalId)).findFirst().get();

        // create copy of original and insert it
        SapConfiguration archived = new SapConfiguration(original);
        archived.setArchived(true);
        archived.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), getUsername());
        insertRecord(archived);

        // reference the new sap config on all critical access queries
        getSapQueries(true).stream().filter(x -> x.getSapConfig().getId().equals(originalId)).forEach(query -> {

            query.setSapConfig(archived);
            updateRecord(query);
        });
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

        TraceOut.enter();

        config.adjustReferences();

        // check if the config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getId().equals(config.getId()));

        if (archive) {

            config.setArchived(true);
            updateRecord(config);

        } else {

            deleteRecord(config);
        }

        TraceOut.leave();
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

        TraceOut.enter();

        pattern.adjustReferences();

        // check if the pattern has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getPatterns().stream().anyMatch(y -> y.getId().equals(pattern.getId())));

        // remove pattern from active configs referencing it
        pattern.getConfigurations().forEach(config -> {

            if (!config.isArchived()) {

                config.getPatterns().remove(pattern);
                pattern.getConfigurations().remove(config);
                updateRecord(config);
            }
        });

        if (archive) {

            // archive pattern
            pattern.setArchived(true);
            updateRecord(pattern);

        } else {

            // delete pattern
            deleteRecord(pattern);
        }

        TraceOut.leave();
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

        TraceOut.enter();

        whitelist.adjustReferences();

        // check if the whitelist has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getConfig().getWhitelist().getId().equals(whitelist.getId()));

        // remove whitelist from active configs referencing it
        getConfigs(false).forEach(config -> {

            config.setWhitelist(null);
            updateRecord(config);
        });

        if (archive) {

            // archive whitelist
            whitelist.setArchived(true);
            updateRecord(whitelist);

        } else {

            // delete whitelist
            deleteRecord(whitelist);
        }

        TraceOut.leave();
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

        TraceOut.enter();

        // check if the sap config has already been used by a critical access query
        boolean archive = getSapQueries(true).stream().anyMatch(x -> x.getSapConfig().getId().equals(config.getId()));

        if (archive) {

            // archive sap config
            config.setArchived(true);
            updateRecord(config);

        } else {

            // delete sap config
            deleteRecord(config);
        }

        TraceOut.leave();
    }

    // ============================================
    //          U S E R   A C C O U N T S
    // ============================================

    /**
     * This method adds a new database user with rights according to the given role.
     *
     * @param user     the account data of the new database user
     * @param password the password of the new database user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void createDatabaseUser(DbUser user, String password) throws Exception {

        TraceOut.enter();

        try (Session session = getSessionFactory().openSession()) {

            Transaction transaction = null;

            try {

                // avoid sql injection with username
                if (user.getUsername().contains(" ")) {
                    // TODO: implement this as custom exception
                    throw new Exception("Invalid username! No whitespaces allowed!");
                }

                transaction = session.beginTransaction();

                // create a new database account
                String sql = "CREATE USER " + user.getUsername() + " PASSWORD :password " + ((user.getRoles().contains(DbUserRole.Admin)) ? "ADMIN" : "");
                session.createNativeQuery(sql).setParameter("password", password).executeUpdate();

                // grant privileges to the database account according to given roles
                user.getRoles().forEach(role -> addDbRole(session, user.getUsername(), role));

                session.flush();
                transaction.commit();

            } catch (Exception ex) {

                if (transaction != null) {
                    transaction.rollback();
                }

                throw ex;
            }
        }

        TraceOut.leave();
    }

    /**
     * This method changes the roles of an existing user.
     *
     * @param user the database user to be updated
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void updateUserRoles(DbUser user) throws Exception {

        TraceOut.enter();

        try (Session session = getSessionFactory().openSession()) {

            Transaction transaction = null;

            try {

                // create a new database account
                DbUser original = getDatabaseUsers().stream().filter(x -> x.getUsername().equals(user.getUsername().toUpperCase())).findFirst().orElse(null);

                if (original != null) {

                    transaction = session.beginTransaction();

                    // grant privileges that were added by admin
                    user.getRoles().stream().filter(x -> !original.getRoles().contains(x)).forEach(role -> addDbRole(session, user.getUsername(), role));

                    // revoke privileges that were removed by admin
                    original.getRoles().stream().filter(x -> !user.getRoles().contains(x)).forEach(role -> removeDbRole(session, user.getUsername(), role));

                    session.flush();
                    transaction.commit();

                } else {
                    // TODO: replace this exception with a custom exception
                    throw new IllegalArgumentException("Database user does not exist.");
                }

            } catch (Exception ex) {

                if (transaction != null) {
                    transaction.rollback();
                }

                throw ex;
            }
        }

        TraceOut.leave();
    }

    private void addDbRole(Session session, String username, DbUserRole role) {

        TraceOut.enter();

        // grant privileges to database account
        session.createNativeQuery("GRANT " + role.toString() + " TO " + username).executeUpdate();

        // grant database admin rights
        if (role == DbUserRole.Admin) {
            session.createNativeQuery("ALTER USER " + username + " ADMIN TRUE").executeUpdate();
        }

        TraceOut.leave();
    }

    private void removeDbRole(Session session, String username, DbUserRole role) {

        TraceOut.enter();

        // revoke privileges from database account
        session.createNativeQuery("REVOKE " + role.toString() + " FROM " + username).executeUpdate();

        // revoke database admin rights
        if (role == DbUserRole.Admin) {
            session.createNativeQuery("ALTER USER " + username + " ADMIN FALSE").executeUpdate();
        }

        TraceOut.leave();
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

        TraceOut.enter();

        try (Session session = getSessionFactory().openSession()) {

            Transaction transaction = null;

            try {

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

        TraceOut.leave();
    }

    /**
     * This method deletes an existing database user.
     *
     * @param username the name of the user to delete
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    @Override
    public void deleteDatabaseUser(String username) throws Exception {

        TraceOut.enter();

        try (Session session = getSessionFactory().openSession()) {

            Transaction transaction = null;

            try {

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

        TraceOut.leave();
    }

    /**
     * This method gets the current logged-in database user.
     *
     * @return the current logged-in user
     * @throws Exception caused by unauthorized access (e.g. missing privileges, wrong login credentials, etc.)
     */
    public DbUser getCurrentUser() throws Exception {

        TraceOut.enter();

        DbUser user =
            getDatabaseUsers().stream()
            .filter(x -> x.getUsername().toUpperCase().equals(getUsername().toUpperCase()))
            .findFirst().get();

        TraceOut.leave();
        return user;
    }

}
