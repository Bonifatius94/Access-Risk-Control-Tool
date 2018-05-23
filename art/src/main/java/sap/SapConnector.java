package sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;
import data.entities.AuthorizationPattern;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
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


    /**
     * Runs multiple SapQueries with a given set patterns.
     *
     * @param patterns the Authorization patterns
     * @return list of users that violate the patterns
     * @throws Exception caused by errors during the sap query
     */
    public List<String> runSapQuery(List<AuthorizationPattern> patterns) throws Exception {
        List<String> result = new ArrayList<>();

        for (AuthorizationPattern pattern : patterns) {
            result.addAll(runSapQuery(pattern));
        }

        return result;
    }

    /**
     * Runs a single SapQuery with a given pattern.
     *
     * @param pattern the Authorization pattern
     * @return list of users that violate the pattern
     * @throws Exception caused by errors during the sap query
     */
    public List<String> runSapQuery(AuthorizationPattern pattern) throws Exception {
        JCoTable result = this.querySapData(pattern);
        ArrayList<String> userList = new ArrayList<>();

        for (int i = 0; i < result.getNumRows(); i++) {

            // get data from record
            String bname = result.getString("BNAME");
            userList.add(bname);

            // write data to console
            // System.out.println("BNAME=" + bname + ", CLASS=" + classname + ", NAME2=" + name2);

            // go to next row
            result.nextRow();
        }

        return userList;
    }

    /**
     * This method queries sap data using the overloaded sap server destination file path.
     *
     * @throws Exception caused by errors during the sap query
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private JCoTable querySapData(AuthorizationPattern pattern) throws Exception {
        if (canPingServer()) {
            JCoDestination destination = JCoDestinationManager.getDestination(config.getServerDestination());
            JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");

            if (function != null) {

                // set query parameters
                setQueryParameters(pattern, function);

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
     * @param function the sap function that gets attached by query parameters
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
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
}
