package ui;

import data.entities.analysis.ConfigurationHistory;
import data.entities.analysis.CriticalAccessList;
import data.entities.config.AccessPattern;
import data.entities.config.Configuration;
import data.entities.config.Whitelist;

import io.msoffice.excel.AccessPatternImportHelper;
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

        SapConfiguration sapConfig = new SapConfiguration();
        sapConfig.setServerDestination("ec2-54-209-137-85.compute-1.amazonaws.com");
        sapConfig.setSysNr("00");
        sapConfig.setClient("001");
        sapConfig.setUser("GROUP_11");
        sapConfig.setPassword("Wir sind das beste Team!");
        sapConfig.setLanguage("EN");
        sapConfig.setPoolCapacity("0");

        try {

            List<AccessPattern> patterns = new AccessPatternImportHelper().importAuthorizationPattern("Example - Zugriffsmuster.xlsx");
            patterns.forEach(x -> System.out.println(x));

            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            whitelist.getEntries().forEach(x -> System.out.println(x));

            // init a new config with the whitelist and access patterns
            Configuration config = new Configuration();
            config.setWhitelist(whitelist);
            config.setPatterns(patterns);

            // convert config to config history
            ConfigurationHistory configHistory = new ConfigurationHistory(config);

            SapConnector connector = new SapConnector(sapConfig);
            CriticalAccessList result = connector.runAnalysis(configHistory);
            result.getEntries().forEach(x -> System.out.println(x));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}