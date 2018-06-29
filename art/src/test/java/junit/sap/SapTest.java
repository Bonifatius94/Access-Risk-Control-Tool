package junit.sap;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;

import extensions.OperatingSystemHelper;
import extensions.progess.IProgressListener;

import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sap.SapConnector;

import setup.AppSetupHelper;
import setup.ZipHelper;

@SuppressWarnings("all")
public class SapTest {

    @BeforeAll
    public static void prepareSapjcoDependencies() throws Exception {

        try {
            new AppSetupHelper().setupApp();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Test
    public void testLogin() {

        boolean ret = false;

        try {

            String username = "GROUP_11";
            String password = "Wir sind das beste Team!";
            SapConfiguration sapConfig = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", "0");

            try (SapConnector conn1 = new SapConnector(sapConfig, username, password)) {

                try (SapConnector conn2 = new SapConnector(sapConfig, username, password)) {

                    ret = conn1.canPingServer() && conn2.canPingServer();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testSapQuery() {

        boolean ret = false;

        // init user credentials
        String username = "GROUP_11";
        String password = "Wir sind das beste Team!";

        // init sap settings (here: test server data)
        SapConfiguration sapConfig = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", "0");

        try {

            // parsing test patterns from excel file
            List<AccessPattern> patterns = new AccessPatternImportHelper().importAccessPatterns("Example - Zugriffsmuster.xlsx");

            // parsing test whitelist from excel file
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

            // init a new config with the whitelist and access patterns
            Configuration config = new Configuration();
            config.setWhitelist(whitelist);
            config.setPatterns(patterns);

            // write config to console
            System.out.println(config);

            // run sap query with config and sap settings
            try (SapConnector connector = new SapConnector(sapConfig, username, password)) {

                connector.register(new IProgressListener() {
                    @Override
                    public void onProgressChanged(double percentage) {
                        System.out.println("Progress at " + (int)(percentage * 100) + "%");
                    }
                });

                CriticalAccessQuery query = connector.runAnalysis(config);
                System.out.println("SAP query results (" + query.getEntries().size() + "):");
                query.getEntries().forEach(x -> System.out.println(x));

                ret =
                    query.getSapConfig().equals(sapConfig)
                    && query.getConfig().equals(config)
                    && query.getConfig().getWhitelist().equals(whitelist)
                    && query.getConfig().getPatterns().containsAll(patterns)
                    && query.getEntries().size() == 24;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        assert(ret);
    }

    /**
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
