package sapdata;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

@SuppressWarnings("WeakerAccess")
public class SapContext {

    /**
     * This method is a test program for testing the connection to a sap server
     *
     * @param args empty args (never used)
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public static void main(String[] args) {

        SapContext context = new SapContext();
        context.runTest();
    }

    /**
     * This method runs the test sequence.
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void runTest() {

        final String serverDestination = "ec2-54-209-137-85.compute-1.amazonaws.com";

        try {
            createServerDestinationFile(serverDestination);

            if (canPingServer(serverDestination)) {
                querySapData(serverDestination);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method creates a new sap server destination file.
     *
     * @param filePath file path of the new sap server destination file
     * @throws Exception caused by file stream operations
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void createServerDestinationFile(String filePath) throws Exception {

        // set config properties
        Properties settings = new Properties();
        settings.setProperty(DestinationDataProvider.JCO_ASHOST, "ec2-54-209-137-85.compute-1.amazonaws.com");
        settings.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
        settings.setProperty(DestinationDataProvider.JCO_CLIENT, "001");
        settings.setProperty(DestinationDataProvider.JCO_USER, "GROUP_11");
        settings.setProperty(DestinationDataProvider.JCO_PASSWD, "Wir sind das beste Team!");
        settings.setProperty(DestinationDataProvider.JCO_LANG, "EN");
        settings.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "0");

        // create file handle of output config file
        File configFile = new File(filePath + ".jcoDestination");

        // write settings to config file
        try (FileOutputStream fos = new FileOutputStream(configFile, false)) {
            settings.store(fos, "for tests only !");
        }
    }

    /**
     * This method pings the sap server specified in the sap server destination file.
     *
     * @param serverDestination file path of the sap server destination file
     * @return a boolean value that indicates whether the ping was successful
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private boolean canPingServer(String serverDestination) {

        boolean ret;

        try {
            JCoDestination destination = JCoDestinationManager.getDestination(serverDestination);
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
     * This method queries sap data using the overloaded sap server destination file path
     *
     * @param serverDestination the file path of the sap server destination file used for the query
     * @throws Exception caused by errors during the sap query
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void querySapData(String serverDestination) throws Exception {

        JCoDestination destination = JCoDestinationManager.getDestination(serverDestination);
        JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");

        if (function != null) {

            // set query parameters
            setQueryParameters(function);

            // run query
            function.execute(destination);

            // write data to console
            JCoTable users = function.getExportParameterList().getTable("ET_USERS");

            for (int i = 0; i < users.getNumRows(); i++) {

                // get data from record
                String bname = users.getString("BNAME");
                String classname = users.getString("CLASS");
                String name2 = users.getString("NAME2");

                // write data to console
                System.out.println("BNAME=" + bname + ", CLASS=" + classname + ", NAME2=" + name2);

                // go to next row
                users.nextRow();
            }
        }
    }

    /**
     * This method initializes the query parameters for the test sap query
     *
     * @param function the sap function that gets attached by query parameters
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void setQueryParameters(JCoFunction function) {

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
    }

}
