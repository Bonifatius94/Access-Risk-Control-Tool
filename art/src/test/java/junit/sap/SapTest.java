package junit.sap;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;

import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import sap.SapConnector;

@SuppressWarnings("all")
public class SapTest {

    @Test
    @Disabled
    public void testSapQuery() {

        // init user credentials
        String username = "GROUP_11";
        String password = "Wir sind das beste Team!";

        // init sap settings (here: test server data)
        SapConfiguration sapConfig = new SapConfiguration();
        sapConfig.setServerDestination("ec2-54-209-137-85.compute-1.amazonaws.com");
        sapConfig.setSysNr("00");
        sapConfig.setClient("001");
        sapConfig.setLanguage("EN");
        sapConfig.setPoolCapacity("0");

        try {

            // parsing test patterns from excel file
            List<AccessPattern> patterns = new AccessPatternImportHelper().importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            // parsing test whitelist from excel file
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

            // init a new config with the whitelist and access patterns
            Configuration config = new Configuration();
            config.setWhitelist(whitelist);
            config.setPatterns(patterns);

            // write config to console
            System.out.println(config);

            // run sap query with config and sap settings
            SapConnector connector = new SapConnector(sapConfig, username, password);
            CriticalAccessQuery query = connector.runAnalysis(config);
            System.out.println("SAP query results:");
            query.getEntries().forEach(x -> System.out.println(x));

            assert(
                query.getSapConfig().equals(sapConfig)
                    && query.getConfig().equals(config)
                    && query.getConfig().getWhitelist().equals(whitelist)
                    && query.getConfig().getPatterns().containsAll(patterns)
                    && query.getEntries().size() == 24

                // TODO: add code to check data equality of critical access entries
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Expected results of the sap query (with duplicates):
     * ===================================================
     * ViolatedUseCaseID: 1.A, UserName: ZT2112_F
     * ViolatedUseCaseID: 1.A, UserName: ZT2113_1_P
     * ViolatedUseCaseID: 1.A, UserName: ZT3222_1_P
     * ViolatedUseCaseID: 1.A, UserName: ZT3223_1_P
     * ViolatedUseCaseID: 1.A, UserName: ZT3223_F
     * ViolatedUseCaseID: 1.A, UserName: ZT3224_1_P
     * ViolatedUseCaseID: 1.A, UserName: ZT3224_F
     * ViolatedUseCaseID: 2.A, UserName: ZT1415_P
     * ViolatedUseCaseID: 2.A, UserName: ZT2113_1_P
     * ViolatedUseCaseID: 2.B, UserName: ZT2112_F
     * ViolatedUseCaseID: 2.B, UserName: ZT2113_1_P
     * ViolatedUseCaseID: 2.B, UserName: ZT2113_2_P
     * ViolatedUseCaseID: 2.B, UserName: ZT2211_1_P
     * ViolatedUseCaseID: 2.B, UserName: ZT2211_2_P
     * ViolatedUseCaseID: 2.B, UserName: ZT4212_P
     * ViolatedUseCaseID: 2.B, UserName: ZT2112_F
     * ViolatedUseCaseID: 2.B, UserName: ZT2211_2_P
     * ViolatedUseCaseID: 2.B, UserName: ZT4212_P
     * ViolatedUseCaseID: 2.C, UserName: ZT2112_F
     * ViolatedUseCaseID: 2.C, UserName: ZT2113_1_P
     * ViolatedUseCaseID: 2.C, UserName: ZT2112_F
     * ViolatedUseCaseID: 2.C, UserName: ZT2113_1_P
     * ViolatedUseCaseID: 2.C, UserName: ZT2313_2_P
     * ViolatedUseCaseID: 3.A, UserName: ZT2112_F
     * ViolatedUseCaseID: 3.B, UserName: ZT2111_F
     * ViolatedUseCaseID: 3.B, UserName: ZT2112_P
     * ViolatedUseCaseID: 3.B, UserName: ZT2113_1_F
     *
     * Expected results of the sap query (without duplicates):
     * ======================================================
     * ViolatedUseCaseID: 1.A, Username: ZT2112_F
     * ViolatedUseCaseID: 1.A, Username: ZT2113_1_P
     * ViolatedUseCaseID: 1.A, Username: ZT3222_1_P
     * ViolatedUseCaseID: 1.A, Username: ZT3223_1_P
     * ViolatedUseCaseID: 1.A, Username: ZT3223_F
     * ViolatedUseCaseID: 1.A, Username: ZT3224_1_P
     * ViolatedUseCaseID: 1.A, Username: ZT3224_F
     * ViolatedUseCaseID: 2.A, Username: ZT1415_P
     * ViolatedUseCaseID: 2.A, Username: ZT2113_1_P
     * ViolatedUseCaseID: 2.A, Username: ZT2313_1_P
     * ViolatedUseCaseID: 2.B, Username: ZT2112_F
     * ViolatedUseCaseID: 2.B, Username: ZT2211_2_P
     * ViolatedUseCaseID: 2.B, Username: ZT4212_P
     * ViolatedUseCaseID: 2.B, Username: ZT2113_1_P
     * ViolatedUseCaseID: 2.B, Username: ZT2113_2_P
     * ViolatedUseCaseID: 2.B, Username: ZT2211_1_P
     * ViolatedUseCaseID: 2.C, Username: ZT2112_F
     * ViolatedUseCaseID: 2.C, Username: ZT2113_1_P
     * ViolatedUseCaseID: 2.C, Username: ZT2313_1_P
     * ViolatedUseCaseID: 2.C, Username: ZT2313_2_P
     * ViolatedUseCaseID: 3.A, Username: ZT2112_F
     * ViolatedUseCaseID: 3.B, Username: ZT2111_F
     * ViolatedUseCaseID: 3.B, Username: ZT2112_P
     * ViolatedUseCaseID: 3.B, Username: ZT2113_1_F
     */

}
