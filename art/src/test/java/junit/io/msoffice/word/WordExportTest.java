package junit.io.msoffice.word;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.ConditionLinkage;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;
import io.msoffice.word.ReportExportHelper;
import io.msoffice.word.ReportExportType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordExportTest {

    @Test
    public void testWordExport() {

        boolean ret = false;

        try {

            // get access patterns from excel file
            AccessPatternImportHelper helper = new AccessPatternImportHelper();
            List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            // get whitelist from excel file
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

            // create configuration
            Configuration config = new Configuration("Test Config", "This is a test description of a config.", patterns, whitelist);

            // create sap configuration
            SapConfiguration sapConfig = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", "0");

            // create critical access query
            CriticalAccessQuery query = new CriticalAccessQuery(config, sapConfig,
                new HashSet<>(Arrays.asList(
                    new CriticalAccessEntry(patterns.get(0), "Test User"),
                    new CriticalAccessEntry(patterns.get(1), "Test User 2"),
                    new CriticalAccessEntry(patterns.get(2), "Test User 3"),
                    new CriticalAccessEntry(patterns.get(2), "Test User 3"),
                    new CriticalAccessEntry(patterns.get(2), "Test User 3")
                ))
            );

          //  System.out.println(query.getEntries().iterator().next().getAccessPattern());

            // export data as docx format
            new ReportExportHelper().exportReport(query, "out.docx", ReportExportType.Word);

            // export data as pdf format
         //   new ReportExportHelper().exportReport(query, "out.pdf", ReportExportType.Pdf);

            // if no exception occurred, the test was successful
            ret = true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(true);
    }

}
