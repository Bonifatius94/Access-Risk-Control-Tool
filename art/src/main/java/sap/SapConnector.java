package sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

import data.entities.AccessCondition;
import data.entities.AccessConditionType;
import data.entities.AccessPattern;
import data.entities.AccessPatternConditionProperty;
import data.entities.ConditionLinkage;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import tools.tracing.TraceOut;

@SuppressWarnings("WeakerAccess")
public class SapConnector implements ISapConnector, Closeable {

    // =============================
    //          constructor
    // =============================

    /**
     * Creates a new SapConnector with the given configuration, username and password.
     *
     * @param sapConfig the configuration
     * @param username the username
     * @param password the password
     * @throws Exception caused by an error during sap server destination initialization
     */
    public SapConnector(SapConfiguration sapConfig, String username, String password) throws Exception {

        TraceOut.enter();

        // init this instance with sap config
        this.sapConfig = sapConfig;
        this.username = username;
        this.password = password;

        // overwrite the JCo SAP DestinationDataProvider so we don't need to create a file
        sessionKey = CustomDestinationDataProvider.getInstance().openSession(sapConfig, username, password);

        TraceOut.leave();
    }

    // =============================
    //           members
    // =============================

    private String sessionKey;

    private SapConfiguration sapConfig;
    private String username;
    private String password;

    // =============================
    //            ping
    // =============================

    /**
    /** This method pings the sap server specified in the sap server config.
     *
     * @return a boolean value that indicates whether the ping was successful
     */
    public boolean canPingServer() {

        TraceOut.enter();

        boolean ret = true;

        try {

            // try to ping the sap server (if the ping fails an exception is thrown -> program enters catch block and returns false)
            JCoDestination destination = JCoDestinationManager.getDestination(sessionKey);
            destination.ping();

        } catch (JCoException ex) {

            TraceOut.writeException(ex);
            ret = false;
        }

        TraceOut.leave();
        return ret;
    }

    // =============================
    //         query logic
    // =============================

    /**
     * This method runs a SAP analysis for the given config.
     *
     * @param config the configuration used for the query (contains a whitelist and a set of access patterns)
     * @return the results of the query (including all configuration settings used for the query)
     * @throws Exception caused by network errors during sap query
     */
    public CriticalAccessQuery runAnalysis(Configuration config) throws Exception {

        TraceOut.enter();

        CriticalAccessQuery query = null;

        if (canPingServer()) {

            Set<CriticalAccessEntry> entries = new HashSet<>();

            for (AccessPattern pattern : config.getPatterns()) {

                // executing query for pattern
                Set<CriticalAccessEntry> resultsOfPattern = runSapQuery(pattern, config.getWhitelist());
                entries.addAll(resultsOfPattern);

                TraceOut.writeInfo("Pattern: " + pattern.getUsecaseId() + ", results count: " + resultsOfPattern.size());
            }

            // write results to critical access query (ready for insertion into database)
            query = new CriticalAccessQuery();
            query.setEntries(entries);
            query.setConfig(config);
            query.setSapConfig(this.sapConfig);
        }

        TraceOut.leave();
        return query;
    }

    /**
     * This method runs a SAP query for the given pattern and applies the given whitelist to the results of the query afterwards.
     *
     * @param pattern the pattern to run the query with
     * @param whitelist the whitelist to be applied to query results
     * @return the list of CriticalAccesses (users)
     */
    private Set<CriticalAccessEntry> runSapQuery(AccessPattern pattern, Whitelist whitelist) throws Exception {

        TraceOut.enter();

        JCoDestination destination = JCoDestinationManager.getDestination(sessionKey);
        JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");

        JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
        JCoTable profileTable = function.getImportParameterList().getTable("IT_PROF1");

        Set<String> usernames = new HashSet<>();

        for (AccessCondition condition : pattern.getConditions()) {

            // apply condition to sap query
            prepareJcoTablesForQuery(condition, inputTable, profileTable);

            JCoTable conditionQueryResultTable = sapQuerySingleCondition(function);
            Set<String> usernamesOfCondition = parseQueryResults(conditionQueryResultTable);

            if (pattern.getLinkage() == ConditionLinkage.And) {

                // intersect lists
                usernames = usernames.isEmpty() ? usernamesOfCondition : usernames.stream().filter(x -> usernamesOfCondition.contains(x)).collect(Collectors.toSet());

            } else if (pattern.getLinkage() == ConditionLinkage.Or || pattern.getLinkage() == ConditionLinkage.None) {

                // union lists
                usernames.addAll(usernamesOfCondition);
            }

            // clear sap input tables
            inputTable.clear();
            profileTable.clear();
        }

        if (whitelist != null) {

            // get whitelisted users
            Set<String> whitelistedUsers = whitelist.getEntries().stream()
                .filter(x -> x.getUsecaseId().equals(pattern.getUsecaseId())).map(x -> x.getUsername()).collect(Collectors.toSet());

            // apply whitelist to query results (remove whitelisted users)
            usernames.removeAll(whitelistedUsers);
        }

        // create critical access entries from remaining usernames + pattern
        Set<CriticalAccessEntry> entries = usernames.stream().map(x -> new CriticalAccessEntry(pattern, x)).collect(Collectors.toSet());

        TraceOut.leave();
        return entries;
    }

    /**
     * Applies the given condition to the input parameter tables.
     *
     * @param condition    the condition that is applied
     * @param inputTable   the inputTable for patterns
     * @param profileTable the inputTable for profiles
     */
    private void prepareJcoTablesForQuery(AccessCondition condition, JCoTable inputTable, JCoTable profileTable) {

        TraceOut.enter();

        if (condition.getType() == AccessConditionType.Profile) {

            profileTable.appendRow();
            profileTable.setValue("SIGN", "I");
            profileTable.setValue("OPTION", "EQ");
            profileTable.setValue("LOW", condition.getProfileCondition().getProfile());

        } else if (condition.getType() == AccessConditionType.Pattern) {

            for (AccessPatternConditionProperty property : condition.getPatternCondition().getProperties()) {

                inputTable.appendRow();
                inputTable.setValue("OBJCT", property.getAuthObject());
                inputTable.setValue("FIELD", property.getAuthObjectProperty());

                // TODO: how to handle empty strings? (this piece of code would not treat them like null -> wrong results?)
                // => suggestion: if (property.getValue1() != null && !property.getValue1().isEmpty())

                if (property.getValue1() != null) {
                    inputTable.setValue("VAL1", property.getValue1());
                }

                if (property.getValue2() != null) {
                    inputTable.setValue("VAL2", property.getValue2());
                }

                if (property.getValue3() != null) {
                    inputTable.setValue("VAL3", property.getValue3());
                }

                if (property.getValue4() != null) {
                    inputTable.setValue("VAL4", property.getValue4());
                }
            }
        }

        TraceOut.leave();
    }

    /**
     * Runs a single Condition query and return its result as a JCoTable.
     *
     * @param function the JCoFunction which has the correct parameters for the request
     * @return the response of the SAP system
     * @throws Exception No connection to SAP possible
     */
    private JCoTable sapQuerySingleCondition(JCoFunction function) throws Exception {

        TraceOut.enter();

        JCoTable results = null;
        JCoDestination destination = JCoDestinationManager.getDestination(sessionKey);

        /*if (canPingServer()) {*/

        if (function != null) {

            // run query
            function.execute(destination);
            results = function.getExportParameterList().getTable("ET_USERS");

        } else {
            throw new Exception("Function could not be initialized!");
        }

        /*} else {
            throw new Exception("Can't connect to the server!");
        }*/

        TraceOut.leave();
        return results;
    }

    /**
     * Gets the usernames causing a critical access warning according to the measured condition.
     *
     * @param table the table that contains the results of the sap query of the current condition
     * @return a set of usernames causing a critical access warning
     */
    private Set<String> parseQueryResults(JCoTable table) {

        TraceOut.enter();

        Set<String> usernames = new HashSet<>();

        for (int i = 0; i < table.getNumRows(); i++) {

            // get data from record
            String bname = table.getString("BNAME");
            usernames.add(bname);

            // go to next row
            table.nextRow();
        }

        TraceOut.leave();
        return usernames;
    }

    // =============================
    //          overrides
    // =============================

    @Override
    public void close() throws IOException {
        CustomDestinationDataProvider.getInstance().closeSession(sessionKey);
    }

}
