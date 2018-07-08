package junit.io.msoffice.word;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;

import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;
import io.msoffice.word.PdfExportHelper;
import io.msoffice.word.WordExportHelper;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class WordExportTest {

    @Test
    @Disabled
    public void testWordExport() {

        boolean ret = false;

        try {

            // get access patterns from excel file
            AccessPatternImportHelper helper = new AccessPatternImportHelper();
            List<AccessPattern> patterns = helper.importAccessPatterns("Example - Zugriffsmuster.xlsx");

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

            // set createdAt + createdBy of query
            query.initCreationFlags(ZonedDateTime.now(ZoneOffset.UTC), "TESTUSER");

            // export data as docx format
            new WordExportHelper().exportDocument(query, new File("out_de.docx"), Locale.GERMAN);

            // export data as docx format
            new WordExportHelper().exportDocument(query, new File("out_en.docx"), Locale.ENGLISH);

            // export data as pdf format
            new PdfExportHelper().exportDocument(query, new File("out_de.pdf"), Locale.GERMAN);

            // export data as pdf format
            new PdfExportHelper().exportDocument(query, new File("out_en.pdf"), Locale.ENGLISH);

            // if no exception occurred, the test was successful
            ret = true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

}
