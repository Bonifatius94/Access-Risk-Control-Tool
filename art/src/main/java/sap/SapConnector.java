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
import data.entities.WhitelistEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class SapConnector {

    private SapConfiguration sapConfig;
    private String username;
    private String password;

    /**
     * Creates a new SapConnector with the given configuration, username and password.
     *
     * @param sapConfig the configuration
     * @param username the username
     * @param password the password
     */
    public SapConnector(SapConfiguration sapConfig, String username, String password) throws Exception {

        // init this instance with sap sapConfig
        this.sapConfig = sapConfig;
        this.username = username;
        this.password = password;

        // overwrite the JCo SAP DestinationDataProvider so we don't need to create a file
        com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(new CustomDestinationDataProvider(sapConfig, username, password));
    }

    /**
     * This method pings the sap server specified in the sap server destination file.
     *
     * @return a boolean value that indicates whether the ping was successful
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private boolean canPingServer() {

        boolean ret;

        try {

            // try to ping the sap server (if the ping fails an exception is thrown -> program enters catch block and returns false)
            JCoDestination destination = JCoDestinationManager.getDestination(sapConfig.getServerDestination());
            destination.ping();
            ret = true;

        } catch (JCoException e) {

            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    // ===================================
    //              QUERY LOGIC
    // ===================================

    /**
     * Runs the analysis with all given AuthorizationPatterns and a whitelist.
     *
     * @param config the configuration used for the query (contains a whitelist and a set of access patterns)
     * @return the resulting list of users after applying the whitelist
     */
    public CriticalAccessQuery runAnalysis(Configuration config) throws Exception {

        // run sap queries (use cases / conditions ...)
        List<CriticalAccessEntry> entries = new ArrayList<>();

        for (AccessPattern pattern : config.getPatterns()) {

            System.out.println("executing sap query for pattern " + pattern.getUsecaseId());
            entries.addAll(runSapQuery(pattern));
        }

        entries = applyWhitelist(entries, config.getWhitelist());

        // init query
        CriticalAccessQuery query = new CriticalAccessQuery();
        query.setEntries(entries);
        query.setConfig(config);
        query.setSapConfig(this.sapConfig);

        return query;
    }

    /**
     * Runs the SAP Query for a complete given pattern.
     *
     * @param pattern the pattern to run the query with
     * @return the list of CriticalAccesses (users)
     */
    private List<CriticalAccessEntry> runSapQuery(AccessPattern pattern) throws Exception {

        // Tupel<CriticalAccessQueryEntry, AccessPattern>

        JCoDestination destination = JCoDestinationManager.getDestination(sapConfig.getServerDestination());
        JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");

        JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
        JCoTable profileTable = function.getImportParameterList().getTable("IT_PROF1");

        List<CriticalAccessEntry> result = new ArrayList<>();
        boolean first = true;
        for (AccessCondition condition : pattern.getConditions()) {

            // apply condition to sap query
            applyConditionToTables(condition, inputTable, profileTable);

            JCoTable partOfResult = sapQuerySingleCondition(function);
            List<CriticalAccessEntry> conditionResults = convertJCoTableToCriticalAccessList(partOfResult, pattern);

            if (pattern.getLinkage() == ConditionLinkage.And) {

                // TODO: use intersect function of lambda if possible

                if (first) {
                    result.addAll(conditionResults);
                } else {
                    result.removeIf(entry -> !conditionResults.contains(entry));
                }

                first = false;

            } else if (pattern.getLinkage() == ConditionLinkage.Or || pattern.getLinkage() == ConditionLinkage.None) {

                // TODO: use lambda expression for this if possible

                // avoid duplicates
                for (CriticalAccessEntry entry : conditionResults) {
                    if (!result.contains(entry)) {
                        result.add(entry);
                    }
                }
            }

            // clear sap input tables
            inputTable.clear();
            profileTable.clear();
        }

        return result;
    }

    /**
     * Applies the condition to the inputParameterTables.
     *
     * @param condition    the condition that is applied
     * @param inputTable   the inputTable for patterns
     * @param profileTable the inputTable for profiles
     */
    private void applyConditionToTables(AccessCondition condition, JCoTable inputTable, JCoTable profileTable) {

        if (condition.getType() == AccessConditionType.ProfileCondition) {

            profileTable.appendRow();
            profileTable.setValue("SIGN", "I");
            profileTable.setValue("OPTION", "EQ");
            profileTable.setValue("LOW", condition.getProfileCondition().getProfile());

        } else if (condition.getType() == AccessConditionType.PatternCondition) {

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
    }

    /**
     * Runs a single Condition query and return its result as a JCoTable.
     *
     * @param function the JCoFunction which has the correct parameters for the request
     * @return the response of the SAP system
     * @throws Exception No connection to SAP possible
     */
    private JCoTable sapQuerySingleCondition(JCoFunction function) throws Exception {

        JCoDestination destination = JCoDestinationManager.getDestination(sapConfig.getServerDestination());

        if (canPingServer()) {

            if (function != null) {
                // run query
                function.execute(destination);

                return function.getExportParameterList().getTable("ET_USERS");
            }

            throw new Exception("Function could not be initialized!");

        } else {
            throw new Exception("Can't connect to the server!");
        }
    }

    /**
     * Applies the whitelist to a given list of usernames.
     *
     * @param entries list of usernames on which whitelist is applied
     * @param whitelist  the whitelist of usernames
     * @return the list of usernames after applying the whitelist
     */
    private List<CriticalAccessEntry> applyWhitelist(List<CriticalAccessEntry> entries, Whitelist whitelist) {

        // TODO: use lambda expression to remove loops if possible

        Iterator<CriticalAccessEntry> iterator = entries.iterator();

        while (iterator.hasNext()) {

            CriticalAccessEntry accessEntry = iterator.next();

            for (WhitelistEntry whitelistEntry : whitelist.getEntries()) {

                if (accessEntry.getAccessPattern().getUsecaseId().equals(whitelistEntry.getUsecaseId())
                    && accessEntry.getUsername().equals(whitelistEntry.getUsername())) {

                    iterator.remove();
                }
            }
        }

        return entries;
    }

    /**
     * Converts a JCoTable into a CriticalAccessQuery. Needs the AuthorizationPattern to correctly annotate the data inside the new list.
     *
     * @param table   the table that needs to be converted as a JCoTable
     * @param pattern the pattern of the JCoTable
     * @return a converted list of CriticalAccesses
     */
    private List<CriticalAccessEntry> convertJCoTableToCriticalAccessList(JCoTable table, AccessPattern pattern) {

        List<CriticalAccessEntry> list = new ArrayList<>();

        for (int i = 0; i < table.getNumRows(); i++) {

            // get data from record
            String bname = table.getString("BNAME");

            // create a new critical access entry and add it to the list
            CriticalAccessEntry temp = new CriticalAccessEntry();
            temp.setAccessPattern(pattern);
            temp.setUsername(bname);
            list.add(temp);

            // go to next row
            table.nextRow();
        }

        return list;
    }

}
