package io.csvexport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;


public class CSVExport {

    /**
     * @param username
     * @param userNr
     * @param criticalAccessQuery
     */




    public static void StartCsvExport(CriticalAccessQuery criticalAccessQuery, String username, String userNr) {

        //CSVPrinter csvPrinter = new CSVPrinter(CSVFormat)














        /* try {

            /**
             *only Testcode , because Analyse View is needed to export probably 
             */
            /*FileReader in = new FileReader("path/to/file.csv");
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("ID", "CustomerNo", "Name").parse(in);
            for (CSVRecord record : records) {
                String id = record.get("ID");
                String customerNo = record.get("CustomerNo");
                String name = record.get("Name");

            }
        } catch (Exception e) {

        }
    }*/
}