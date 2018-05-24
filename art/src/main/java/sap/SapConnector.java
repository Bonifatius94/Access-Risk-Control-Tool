package sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;
import data.entities.*;

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

        /*
        settings.setProperty(DestinationDataProvider.JCO_ASHOST, "ec2-54-209-137-85.compute-1.amazonaws.com");
        settings.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
        settings.setProperty(DestinationDataProvider.JCO_CLIENT, "001");
        settings.setProperty(DestinationDataProvider.JCO_USER, "GROUP_11");
        settings.setProperty(DestinationDataProvider.JCO_PASSWD, "Wir sind das beste Team!");
        settings.setProperty(DestinationDataProvider.JCO_LANG, "EN");
        settings.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "0");
         */


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


    private CriticalAccessList applyWhitelist(CriticalAccessList accessList, Whitelist whitelist) {
        Iterator<CriticalAccessEntry> iterator = accessList.getEntries().iterator();
        while (iterator.hasNext()) {
            CriticalAccessEntry entry = iterator.next();
            if (whitelist.getEntries().contains(entry)) {
                iterator.remove();
            }
        }
        return accessList;
    }

    /**
     * This method queries sap data using the overloaded sap server destination file path.
     *
     * @throws Exception caused by errors during the sap query
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
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
     * This method initializes the query parameters for the sap query.
     *
     * @param pattern the sap function that gets attached by query parameters
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private List<CriticalAccessList> runSapQuery(AuthorizationPattern pattern) {
        try {
            JCoDestination destination = JCoDestinationManager.getDestination(config.getServerDestination());
            JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");

            JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
            JCoTable profileTable = function.getImportParameterList().getTable("IT_PROF1");

            List<CriticalAccessList> result = new ArrayList<>();

            for (ICondition ic : pattern.getConditions()) {
                if (ic instanceof AuthorizationProfileCondition) {
                    AuthorizationProfileCondition condition = (AuthorizationProfileCondition) ic;
                    profileTable.appendRow();
                    profileTable.setValue("SIGN", "I");
                    profileTable.setValue("OPTION", "EQ");
                    profileTable.setValue("LOW", condition.getAuthorizationProfile());
                } else if (ic instanceof AuthorizationPatternCondition) {
                    AuthorizationPatternCondition condition = (AuthorizationPatternCondition) ic;
                    for (AuthorizationPatternConditionProperty property : condition.getProperties()) {
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
                result.add(convert(partOfResult, pattern));
                inputTable.clear();
                profileTable.clear();
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private CriticalAccessList convert(JCoTable table, AuthorizationPattern pattern) {
        CriticalAccessList userList = new CriticalAccessList();

        for (int i = 0; i < table.getNumRows(); i++) {
            // get data from record
            String bname = table.getString("BNAME");

            CriticalAccessEntry temp = new CriticalAccessEntry();
            temp.setAuthorizationPattern(pattern);
            temp.setUserName(bname);

            userList.getEntries().add(temp);

            // write data to console
            // System.out.println("BNAME=" + bname + ", CLASS=" + classname + ", NAME2=" + name2);

            // go to next row
            table.nextRow();
        }

        return userList;
    }


    public CriticalAccessList runAnalysis(List<AuthorizationPattern> patterns, Whitelist whitelist) {
        CriticalAccessList result = new CriticalAccessList();

        for (AuthorizationPattern p : patterns) {
            List<CriticalAccessList> sapResult = runSapQuery(p);

            boolean first = true;
            for (CriticalAccessList list : sapResult) {
                if (p.getLinkage() == ConditionLinkage.And) {
                    if (first) {
                        result.getEntries().addAll(list.getEntries());
                    } else {
                        Iterator<CriticalAccessEntry> iterator = result.getEntries().iterator();
                        while (iterator.hasNext()) {
                            CriticalAccessEntry entry = iterator.next();
                            if (!list.getEntries().contains(entry)) {
                                iterator.remove();
                            }
                        }
                    }
                    first = false;
                } else if (p.getLinkage() == ConditionLinkage.Or || p.getLinkage() == ConditionLinkage.None) {
                    result.getEntries().addAll(list.getEntries());
                }
            }
        }

//        return applyWhitelist(result, whitelist);
        return result;
    }

    /**
     * This method initializes the query parameters for the sap query.
     *
     * @param function the sap function that gets attached by query parameters
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    /*private void setQueryParameters(AuthorizationPattern pattern, JCoFunction function) {
        try {
            JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
            List<AuthorizationPatternConditionProperty> allPatternProps = new ArrayList<>();
            List<String> allProfiles = new ArrayList<>();


            boolean first = true;
            for (ICondition ic : pattern.getConditions()) {
                if (ic instanceof AuthorizationProfileCondition) {
                    AuthorizationProfileCondition condition = (AuthorizationProfileCondition) ic;
                    allProfiles.add(condition.getAuthorizationProfile());
                } else if (ic instanceof AuthorizationPatternCondition) {
                    AuthorizationPatternCondition condition = (AuthorizationPatternCondition) ic;
                    if (pattern.getLinkage() == ConditionLinkage.And) {
                        for (AuthorizationPatternConditionProperty property : condition.getProperties()) {
                            if (first) {
                                allPatternProps.add(property);
                            } else {
                                if (allPatternProps.contains(property)) {
                                    allPatternProps.remove(property);
                                } else {
                                    allPatternProps.add(property);
                                }
                            }
                        }
                        first = false;
                    } else if (pattern.getLinkage() == ConditionLinkage.Or) {
                        allPatternProps.addAll(condition.getProperties());
                    }
                }
            }

            for (AuthorizationPatternConditionProperty property : allPatternProps) {
                inputTable.appendRow();
                inputTable.setValue("OBJCT", property.getAuthObject());
                inputTable.setValue("FIELD", property.getAuthObjectProperty());
                if (property.getValue1() != null) inputTable.setValue("VAL1", property.getValue1());
                if (property.getValue2() != null) inputTable.setValue("VAL2", property.getValue2());
                if (property.getValue3() != null) inputTable.setValue("VAL3", property.getValue3());
                if (property.getValue4() != null) inputTable.setValue("VAL4", property.getValue4());
            }

            JCoTable profileTable = function.getImportParameterList().getTable("IT_PROF1");
            for (String profile: allProfiles) {
                profileTable.appendRow();
                profileTable.setValue("SIGN", "I");
                profileTable.setValue("LOW", "EQ");
                profileTable.setValue("LOW", profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

/**
 * This method initializes the query parameters for the sap query.
 *
 * @param function the sap function that gets attached by query parameters
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
    /*
    private void setQueryParameters(AuthorizationPattern pattern, JCoFunction function) {
        try {
            JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");

            inputTable.appendRow();
            inputTable.setValue("OBJCT", "S_TCODE");
            inputTable.setValue("FIELD", "");
            inputTable.setValue("VAL1", "SU01");

            inputTable.appendRow();
            inputTable.setValue("OBJCT", "S_USER_GRP");
            inputTable.setValue("FIELD", "CLASS");
            inputTable.setValue("VAL1", "SUPER");

            inputTable.appendRow();
            inputTable.setValue("OBJCT", "S_USER_GRP");
            inputTable.setValue("FIELD", "ACTVT");
            inputTable.setValue("VAL1", "01");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */
}
