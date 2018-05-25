package ui;

import data.entities.AuthPattern;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessList;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;
import io.msoffice.excel.AuthorizationPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;
import sap.SapConfiguration;
import sap.SapConnector;


public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // init global exception handling
        Thread.currentThread().setUncaughtExceptionHandler(this::unhandledExceptionOccurred);

        // test sap queries
        testSapStuff();
    }

    private void unhandledExceptionOccurred(Thread thread, Throwable e) {

        // write thread id and exception to console log
        System.out.println("Thread ID: " + thread.getId());
        System.out.println("Stack Trace:");
        System.out.println(e.getMessage());
    }

    /**
     * Testing Sap Stuff.
     */
    public void testSapStuff() {
        SapConfiguration config = new SapConfiguration();
        config.setServerDestination("ec2-54-209-137-85.compute-1.amazonaws.com");
        config.setSysNr("00");
        config.setClient("001");
        config.setUser("GROUP_11");
        config.setPassword("Wir sind das beste Team!");
        config.setLanguage("EN");
        config.setPoolCapacity("0");

        try {
            List<AuthPattern> patterns = new AuthorizationPatternImportHelper().importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            for (AuthPattern p : patterns) {
                System.out.println(p);
            }

            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

            for (WhitelistEntry w : whitelist.getEntries()) {
                System.out.println(w);
            }

            SapConnector connector = new SapConnector(config);
            CriticalAccessList result = connector.runAnalysis(patterns, whitelist);

            for (CriticalAccessEntry entry : result.getEntries()) {
                System.out.println(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}