import data.entities.*;
import io.msoffice.excel.AuthorizationPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;
import sap.SapConfiguration;
import sap.SapConnector;

import java.util.List;

public class Main {
    public static void main(String... args) {
        System.out.println("Hello world!");

        SapConfiguration config = new SapConfiguration();
        config.setServerDestination("ec2-54-209-137-85.compute-1.amazonaws.com");
        config.setSysNr("00");
        config.setClient("001");
        config.setUser("GROUP_11");
        config.setPassword("Wir sind das beste Team!");
        config.setLanguage("EN");
        config.setPoolCapacity("0");

        try {
            List<AuthorizationPattern> patterns = new AuthorizationPatternImportHelper().importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            for (AuthorizationPattern p : patterns) {
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
