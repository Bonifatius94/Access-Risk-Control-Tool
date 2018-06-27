package io.csvexport;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvExport {

    /**
     * Starts Csv Export with an criticalAccessQuery.
     *
     * @param criticalAccessQuery is the result of an anlysis
     * @param file is the File, where the critical entries are written in
     * @param language is the language of the export format
     * @throws IOException caused by incompatible file type or failed write process and other I/O exceptions
     */
    public void startCsvExport(CriticalAccessQuery criticalAccessQuery, File file, Locale language) throws IOException {

        try (FileWriter csvWriter = new FileWriter(file)) {

            // prepare csv format
            char separator = language.equals(Locale.GERMAN) ? ';' : ',';
            CSVFormat format = CSVFormat.DEFAULT.withDelimiter(separator).withHeader("CriticalUserID", "CriticalUserName");

            try (CSVPrinter csvPrinter = new CSVPrinter(csvWriter, format)) {

                // write critical access entries to file
                for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
                    csvPrinter.printRecord(criticalAccessEntry.getAccessPattern().getUsecaseId(), criticalAccessEntry.getUsername());
                }

                // make sure the output stream gets flushed
                csvPrinter.flush();
            }
        }
    }

}