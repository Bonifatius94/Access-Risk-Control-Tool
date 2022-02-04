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
import data.entities.AccessProfileCondition;
import data.entities.ConditionLinkage;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;

import extensions.progess.ProgressableBase;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import tools.tracing.TraceOut;

@SuppressWarnings("WeakerAccess")
public class SapConnector extends ProgressableBase implements ISapConnector, Closeable {

    // =============================
    //          constructor
    // =============================

    /**
     * Creates a new SapConnector with the given configuration, username and password.
     *
     * @param sapConfig the configuration
     * @param username  the username
     * @param password  the password
     * @throws Exception caused by an error during sap server destination initialization
     */
    public SapConnector(SapConfiguration sapConfig, String username, String password) throws Exception {

        TraceOut.enter();

        // init this instance with sap config
        this.sapConfig = sapConfig;

        // overwrite the JCo SAP DestinationDataProvider so we don't need to create a file
        sessionKey = CustomDestinationDataProvider.getInstance().openSession(sapConfig, username, password);

        /*// make sure the connection is initialized (to avoid long queries)
        if (canPingServer()) {
            runTestConditionQuery();
        }*/

        TraceOut.leave();
    }

    // =============================
    //           members
    // =============================

    private String sessionKey;
    private SapConfiguration sapConfig;

    // =============================
    //            ping
    // =============================

    /**
     * /** This method pings the sap server specified in the sap server config.
     * @return a boolean value that indicates whether the ping was successful
     * @throws JCoException if the ping fails
     */
    public synchronized boolean canPingServer() throws JCoException {

        TraceOut.enter();

        // try to ping the sap server (if the ping fails an exception is thrown)
        JCoDestination destination = JCoDestinationManager.getDestination(sessionKey);
        destination.ping();

        TraceOut.leave();
        return true;
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
    public synchronized CriticalAccessQuery runAnalysis(Configuration config) throws Exception {

        TraceOut.enter();

        CriticalAccessQuery query;

        if (config.getPatterns().size() > 0 && canPingServer()) {

            setTotalProgressSteps(config.getPatterns().stream().mapToInt(x -> x.getConditions().size()).sum() + 1);
            Set<CriticalAccessEntry> entries = new LinkedHashSet<>();

            runTestConditionQuery();

            // execute analysis for each pattern parallel
            ExecutorService executor = Executors.newFixedThreadPool(config.getPatterns().size());

            List<Future<Set<CriticalAccessEntry>>> taskCallbacks
                = executor.invokeAll(config.getPatterns().stream()
                .map(pattern -> (Callable<Set<CriticalAccessEntry>>) () -> runPatternAnalysis(pattern, config.getWhitelist()))
                .collect(Collectors.toList()));

            // evaluate query results
            for (Future<Set<CriticalAccessEntry>> callback : taskCallbacks) {
                entries.addAll(callback.get());
            }

            // write results to critical access query (ready for insertion into database)
            query = new CriticalAccessQuery(config, sapConfig, entries);

            resetProgress();

        } else {
            throw new IllegalArgumentException("invalid configuration or server unavailable");
        }

        TraceOut.leave();
        return query;
    }

    /**
     * This method runs a SAP query for the given pattern and applies the given whitelist to the results of the query afterwards.
     *
     * @param pattern   the pattern to run the query with
     * @param whitelist the whitelist to be applied to query results
     * @return the list of CriticalAccesses (users)
     */
    private Set<CriticalAccessEntry> runPatternAnalysis(AccessPattern pattern, Whitelist whitelist) throws Exception {

        TraceOut.enter();

        ExecutorService executor = Executors.newFixedThreadPool(pattern.getConditions().size());
        List<Future<Set<String>>> taskCallbacks =
            executor.invokeAll(pattern.getConditions().stream().map(condition -> prepareSapConditionQueryTask(condition)).collect(Collectors.toSet()));

        // evaluate query results
        Set<String> usernames = new LinkedHashSet<>();

        for (Future<Set<String>> callback : taskCallbacks) {

            Set<String> usernamesToAdd = callback.get();

            if (pattern.getLinkage() == ConditionLinkage.And) {

                // intersect lists
                usernames = usernames.isEmpty() ? usernamesToAdd : usernames.stream().filter(x -> usernamesToAdd.contains(x)).collect(Collectors.toSet());

            } else if (pattern.getLinkage() == ConditionLinkage.Or || pattern.getLinkage() == ConditionLinkage.None) {

                // union lists
                usernames.addAll(usernamesToAdd);
            }
        }

        if (whitelist != null) {

            // get whitelisted users
            Set<String> whitelistedUsers = whitelist.getEntries().stream()
                .filter(x -> x.getUsecaseId().equals(pattern.getUsecaseId())).map(x -> x.getUsername()).collect(Collectors.toSet());

            // apply whitelist to query results (remove whitelisted users)
            usernames.removeAll(whitelistedUsers);
        }

        // create critical access entries from remaining usernames + pattern
        Set<CriticalAccessEntry> entries = usernames.stream().map(username -> new CriticalAccessEntry(pattern, username)).collect(Collectors.toSet());

        TraceOut.leave();
        return entries;
    }

    private void runTestConditionQuery() throws Exception {

        AccessCondition condition = new AccessCondition(null, new AccessProfileCondition("test"));
        Callable<Set<String>> queryTask = prepareSapConditionQueryTask(condition);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.invokeAll(Arrays.asList(queryTask));
    }

    private Callable<Set<String>> prepareSapConditionQueryTask(AccessCondition condition) {

        return () -> {

            //System.out.println("task " + Thread.currentThread().getName() + " started");

            // prepare the sap condition query
            JCoDestination destination = JCoDestinationManager.getDestination(sessionKey);
            JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");
            JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
            JCoTable profileTable = function.getImportParameterList().getTable("IT_PROF1");
            prepareJcoTablesForQuery(condition, inputTable, profileTable);

            // run the query and apply the results to the list
            JCoTable conditionQueryResultTable = sapQuerySingleCondition(function);
            Set<String> usernamesOfCondition = parseQueryResults(conditionQueryResultTable);

            // notify progress
            stepProgress();

            //System.out.println("task " + Thread.currentThread().getName() + " finished");

            return usernamesOfCondition;
        };
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

            for (AccessPatternConditionProperty property : condition.getPatternCondition().getProperties()
                .stream().sorted(Comparator.comparing(AccessPatternConditionProperty::getIndex)).collect(Collectors.toList())) {

                inputTable.appendRow();
                inputTable.setValue("OBJCT", property.getAuthObject());
                inputTable.setValue("FIELD", property.getAuthObjectProperty());

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

        if (function != null) {

            // run query
            function.execute(destination);
            results = function.getExportParameterList().getTable("ET_USERS");

        } else {
            throw new Exception("Function could not be initialized!");
        }

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

        // TODO: parse more data columns

        Set<String> usernames = new LinkedHashSet<>();

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

        TraceOut.enter();

        CustomDestinationDataProvider.getInstance().closeSession(sessionKey);

        TraceOut.leave();
    }

}
