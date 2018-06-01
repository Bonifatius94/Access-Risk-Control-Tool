package sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


@SuppressWarnings("WeakerAccess")
public class SapConnector {

    private SapConfiguration config;
    private String username;
    private String password;

    /**
     * Creates a new SapConnector with the given configuration.
     *
     * @param config the configuration
     * @throws Exception lol
     */
    public SapConnector(SapConfiguration config, String username, String password) throws Exception {

        // init this instance with sap config
        this.config = config;
        this.username = username;
        this.password = password;

        this.createServerDestinationFile();
    }

    /**
     * This method creates a new sap server destination file.
     *
     * @throws Exception caused by file stream operations
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void createServerDestinationFile() throws Exception {

        // set config properties
        Properties settings = new Properties();
        settings.setProperty(DestinationDataProvider.JCO_ASHOST, config.getServerDestination());
        settings.setProperty(DestinationDataProvider.JCO_SYSNR, config.getSysNr());
        settings.setProperty(DestinationDataProvider.JCO_CLIENT, config.getClient());
        settings.setProperty(DestinationDataProvider.JCO_USER, username);
        settings.setProperty(DestinationDataProvider.JCO_PASSWD, password);
        settings.setProperty(DestinationDataProvider.JCO_LANG, config.getLanguage());
        settings.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, config.getPoolCapacity());

        // create file handle of output config file
        File configFile = new File(config.getServerDestination() + ".jcoDestination");

        // write settings to config file
        try (FileOutputStream fos = new FileOutputStream(configFile, false)) {
            settings.store(fos, "for tests only !");
        } catch (Exception e) {
            throw new Exception("Could not store the settings!");
        }
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
            JCoDestination destination = JCoDestinationManager.getDestination(config.getServerDestination());
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

        CriticalAccessQuery result = new CriticalAccessQuery();

        for (AccessPattern pattern : config.getPatterns()) {

            List<CriticalAccessQuery> sapResult = runSapQuery(pattern);
            boolean first = true;

            for (CriticalAccessQuery list : sapResult) {

                if (pattern.getLinkage() == ConditionLinkage.And) {

                    if (first) {
                        result.getEntries().addAll(list.getEntries());
                    } else {
                        result.getEntries().removeIf(entry -> !list.getEntries().contains(entry));
                    }

                    first = false;

                } else if (pattern.getLinkage() == ConditionLinkage.Or || pattern.getLinkage() == ConditionLinkage.None) {
                    result.getEntries().addAll(list.getEntries());
                }
            }
        }

        return applyWhitelist(result, config.getWhitelist());
    }

    /**
     * Runs the SAP Query for a complete given pattern.
     *
     * @param pattern the pattern to run the query with
     * @return the list of CriticalAccesses (users)
     */
    private List<CriticalAccessQuery> runSapQuery(AccessPattern pattern) throws Exception {

        JCoDestination destination = JCoDestinationManager.getDestination(config.getServerDestination());
        JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");

        JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
        JCoTable profileTable = function.getImportParameterList().getTable("IT_PROF1");

        List<CriticalAccessQuery> result = new ArrayList<>();

        for (AccessCondition condition : pattern.getConditions()) {

            applyConditionToTables(condition, inputTable, profileTable);

            JCoTable partOfResult = sapQuerySingleCondition(function);
            result.add(convertJCoTableToCriticalAccessList(partOfResult, pattern));
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

        JCoTable table;
        JCoDestination destination = JCoDestinationManager.getDestination(config.getServerDestination());

        if (canPingServer()) {

            // abort if query was not initialized
            if (function == null) {
                throw new Exception("Function could not be initialized!");
            }

            // run query
            function.execute(destination);
            table = function.getExportParameterList().getTable("ET_USERS");

        } else {
            throw new Exception("Can't connect to the server!");
        }

        return table;
    }

    /**
     * Applies the whitelist to a given list of usernames.
     *
     * @param accessList list of usernames on which whitelist is applied
     * @param whitelist  the whitelist of usernames
     * @return the list of usernames after applying the whitelist
     */
    private CriticalAccessQuery applyWhitelist(CriticalAccessQuery accessList, Whitelist whitelist) {

        // TODO: use lambda expression to remove loops if possible

        Iterator<CriticalAccessEntry> iterator = accessList.getEntries().iterator();

        while (iterator.hasNext()) {

            CriticalAccessEntry accessEntry = iterator.next();

            for (WhitelistEntry whitelistEntry : whitelist.getEntries()) {

                if (accessEntry.getAccessPattern().getUsecaseId().equals(whitelistEntry.getUsecaseId())
                    && accessEntry.getUsername().equals(whitelistEntry.getUsername())) {

                    iterator.remove();
                }
            }
        }

        return accessList;
    }

    /**
     * Converts a JCoTable into a CriticalAccessQuery. Needs the AuthorizationPattern to correctly annotate the data inside the new list.
     *
     * @param table   the table that needs to be converted as a JCoTable
     * @param pattern the pattern of the JCoTable
     * @return a converted list of CriticalAccesses
     */
    private CriticalAccessQuery convertJCoTableToCriticalAccessList(JCoTable table, AccessPattern pattern) {

        CriticalAccessQuery list = new CriticalAccessQuery();

        for (int i = 0; i < table.getNumRows(); i++) {

            // get data from record
            String bname = table.getString("BNAME");

            // create a new critical access entry and add it to the list
            CriticalAccessEntry temp = new CriticalAccessEntry();
            temp.setAccessPattern(pattern);
            temp.setUsername(bname);
            list.getEntries().add(temp);

            // go to next row
            table.nextRow();
        }

        return list;
    }

}
