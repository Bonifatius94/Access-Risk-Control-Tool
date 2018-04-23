package sap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

// TODO: remove suppress warnings annotation
@SuppressWarnings("all")
public class SapTest {

//    private void connectToSap(String username, String password) {
//
//        final String server = "";
//        final String sapProvider = "";
//
//        // Set properties for system connection
//        Properties connectionProperties = new Properties()
//            .setProperty(DestinationDataProvider.JCO_ASHOST, sapProvider)
//            .setProperty(DestinationDataProvider.JCO_SYSNR, "00")
//            .setProperty(DestinationDataProvider.JCO_CLIENT, "100")
//            .setProperty(DestinationDataProvider.JCO_USER, username)
//            .setProperty(DestinationDataProvider.JCO_PASSWD, password)
//            .setProperty(DestinationDataProvider.JCO_LANG , "EN")
//        ;
//
//        // TODO: set JCO_POOL_CAPACITY to 0 (this allows to open unlimited connection)
//
//        // write properties to file
//        createDestinationDataFile(server, connectionProperties);
//
//        // Connect to SAP
//        try {
//            JCoDestination destination = JCoDestinationManager.getDestination(server);
//            System.out.println("Attributes:");
//            System.out.println(destination.getAttributes());
//            System.out.println();
//            destination.ping();
//        }
//        catch (JCoException e) {
//            e.printStackTrace ();
//        }
//
//        // pass parameters and call ABAP function
//        try {
//            // Set ABAP function module
//            JCoDestination destination = JCoDestinationManager.getDestination(server_destination);
//            JCoFunction function = destination.getRepository().getFunction("SUSR_SUIM_API_RSUSR002");
//
//            if (function == null) {
//                throw new RuntimeException("Not found in SAP.");
//            }
//
//            try {
//                // Set importing parameters
//                JCoTable inputTable = function.getImportParameterList().getTable("IT_VALUES");
//                inputTable.appendRow();
//                inputTable.setValue("OBJCT", "S_TCODE");
//                inputTable.setValue("FIELD", "");
//                inputTable.setValue("VAL1", "SU01");
//                inputTable.appendRow();
//                inputTable.setValue("OBJCT", "S_USER_GRP");
//                inputTable.setValue("FIELD", "CLASS");
//                inputTable.setValue("VAL1", "SUPER");
//                inputTable.appendRow();
//                inputTable.setValue("OBJCT", "S_USER_GRP");
//                inputTable.setValue("FIELD", "ACTVT");
//                inputTable.setValue("VAL1", "01");
//
//                // Execute ABAP Function
//                function.execute(destination);
//            }
//            catch (AbapException e) {
//                System.out.println(e.toString());
//                return;
//            }
//
//            // Print the users table
//            JCoTable users = null;
//            users = function.getExportParameterList ().getTable(" ET_USERS ");
//
//            for (int i = 0; i < users.getNumRows(); i++) {
//                // Do something with user data
//                users.nextRow();
//            }
//        }
//        catch (JCoException e) {
//            System.out.println (e.toString());
//            return;
//        }
//    }
//
//    private static void createDestinationDataFile(String destinationName, Properties connectProperties) {
//
//        File destCfg = new File(destinationName + ".jcoDestination");
//
//        try {
//            FileOutputStream fos = new FileOutputStream(destCfg, false);
//            connectProperties.store(fos, "for tests only !");
//            fos.close();
//        }
//        catch (Exception e) {
//            throw new RuntimeException("Unable to create the destination files", e);
//        }
//    }

}
