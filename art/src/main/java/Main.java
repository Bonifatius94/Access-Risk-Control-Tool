import data.entities.AuthorizationPattern;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessList;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import io.msoffice.excel.AuthorizationPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;

import java.util.List;

import sap.SapConfiguration;
import sap.SapConnector;

public class Main {

    /**
     * Test the main functionality.
     */
    public static void main(String... args) {
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
