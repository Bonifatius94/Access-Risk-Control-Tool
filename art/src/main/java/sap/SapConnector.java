package sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

import data.entities.AuthCondition;
import data.entities.AuthConditionType;
import data.entities.AuthPattern;
import data.entities.AuthPatternConditionProperty;
import data.entities.ConditionLinkage;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessList;
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

    public SapConnector(SapConfiguration config) throws Exception {

        // init this instance with sap config
        this.config = config;
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
        settings.setProperty(DestinationDataProvider.JCO_USER, config.getUser());
        settings.setProperty(DestinationDataProvider.JCO_PASSWD, config.getPassword());
        settings.setProperty(DestinationDataProvider.JCO_LANG, config.getLanguage());
        settings.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, config.getPoolCapacity());

        // create file handle of output config file
        File configFile = new File(config.getServerDestination() + ".jcoDestination");

        // write settings to config file
        try (FileOutputStream fos = new FileOutputStream(configFile, false)) {
            settings.store(fos, "for tests only !");
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

            // try to ping sap server (if
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
     * @param patterns  the given patterns to run the analysis on
     * @param whitelist the given whitelist
     * @return the resulting list of users after applying the whitelist
     */
    public CriticalAccessList runAnalysis(List<AuthPattern> patterns, Whitelist whitelist) {

        CriticalAccessList result = new CriticalAccessList();

        for (AuthPattern p : patterns) {

            List<CriticalAccessList> sapResult = runSapQuery(p);
            boolean first = true;

            for (CriticalAccessList list : sapResult) {

                if (p.getLinkage() == ConditionLinkage.And) {

                    if (first) {
                        result.getEntries().addAll(list.getEntries());
                    } else {
                        result.getEntries().removeIf(entry -> !list.getEntries().contains(entry));
                    }

                    first = false;

                } else if (p.getLinkage() == ConditionLinkage.Or || p.getLinkage() == ConditionLinkage.None) {
                    result.getEntries().addAll(list.getEntries());
                }
            }
        }

        return applyWhitelist(result, whitelist);
    }

    /**
     * Runs the SAP Query for a complete given pattern.
     *
     * @param pattern the pattern to run the query with
     * @return the list of CriticalAccesses (users)
     */
    private List<CriticalAccessList> runSapQuery(AuthPattern pattern) {

        try {

            JCoDestination destination = JCoDestinationManager.getDestination(config.getServerDestination());
            JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");

            JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
            JCoTable profileTable = function.getImportParameterList().getTable("IT_PROF1");

            List<CriticalAccessList> result = new ArrayList<>();

            for (AuthCondition condition : pattern.getConditions()) {

                if (condition.getType() == AuthConditionType.ProfileCondition) {

                    profileTable.appendRow();
                    profileTable.setValue("SIGN", "I");
                    profileTable.setValue("OPTION", "EQ");
                    profileTable.setValue("LOW", condition.getProfileCondition().getProfile());

                } else if (condition.getType() == AuthConditionType.PatternCondition) {

                    for (AuthPatternConditionProperty property : condition.getPatternCondition().getProperties()) {

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

                JCoTable partOfResult = sapQuerySingleCondition(function);
                result.add(convertJCoTableToCriticalAccessList(partOfResult, pattern));
                inputTable.clear();
                profileTable.clear();
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
        JCoDestination destination = JCoDestinationManager.getDestination(config.getServerDestination());

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
     * @param accessList list of usernames on which whitelist is applied
     * @param whitelist  the whitelist of usernames
     * @return the list of usernames after applying the whitelist
     */
    private CriticalAccessList applyWhitelist(CriticalAccessList accessList, Whitelist whitelist) {

        Iterator<CriticalAccessEntry> iterator = accessList.getEntries().iterator();

        while (iterator.hasNext()) {

            CriticalAccessEntry accessEntry = iterator.next();

            for (WhitelistEntry whitelistEntry : whitelist.getEntries()) {

                if (accessEntry.getAuthorizationPattern().getUsecaseId().equals(whitelistEntry.getUsecaseId())
                    && accessEntry.getUserName().equals(whitelistEntry.getUsername())) {

                    iterator.remove();
                }
            }
        }

        return accessList;
    }

    /**
     * Converts a JCoTable into a CriticalAccessList. Needs the AuthorizationPattern to correctly annotate the data inside the new list.
     *
     * @param table   the table that needs to be converted as a JCoTable
     * @param pattern the pattern of the JCoTable
     * @return a converted list of CriticalAccesses
     */
    private CriticalAccessList convertJCoTableToCriticalAccessList(JCoTable table, AuthPattern pattern) {

        CriticalAccessList list = new CriticalAccessList();

        for (int i = 0; i < table.getNumRows(); i++) {

            // get data from record
            String bname = table.getString("BNAME");

            // create a new critical access entry and add it to the list
            CriticalAccessEntry temp = new CriticalAccessEntry();
            temp.setAuthorizationPattern(pattern);
            temp.setUserName(bname);
            list.getEntries().add(temp);

            // go to next row
            table.nextRow();
        }

        return list;
    }

}
