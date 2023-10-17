package junit.io.msoffice.csv;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import io.msoffice.csv.CsvExportHelper;
import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public class CsvExportTest {

    @Test
    public void testCsvExport() {

        boolean ret = false;

        try {

            // get access patterns from excel file
            List<AccessPattern> patterns = new AccessPatternImportHelper().importAccessPatterns("Example - Zugriffsmuster.xlsx");

            // get whitelist from excel file
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

            // create configuration
            Configuration config = new Configuration("Test Config", "This is a test description of a config.", patterns, whitelist);

            // create sap configuration
            SapConfiguration sapConfig = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", "0");

            // create critical access query
            CriticalAccessQuery query = new CriticalAccessQuery(config, sapConfig,
                new LinkedHashSet<>(Arrays.asList(
                    new CriticalAccessEntry(patterns.get(0), "Test User"),
                    new CriticalAccessEntry(patterns.get(1), "Test User 2")
                ))
            );

            // set createdAt + createdBy of query
            query.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), "TESTUSER");

            // export data with german format
            new CsvExportHelper().exportDocument(query, new File("out_de.csv"), Locale.GERMAN);

            // export data with english format
            new CsvExportHelper().exportDocument(query, new File("out_en.csv"), Locale.ENGLISH);

            // if no exception occurred, the test was successful
            ret = true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

}
