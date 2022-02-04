package io.msoffice.csv;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import io.msoffice.IReportExportHelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvExportHelper implements IReportExportHelper {

    /**
     * This method exports a critical access query to the given file with the given export format.
     *
     * @param criticalAccessQuery is the result of an anlysis
     * @param file                is the File, where the critical entries are written in
     * @param language            is the language of the export format
     * @throws Exception caused by incompatible file type or failed write process and other I/O exceptions
     */
    @Override
    public void exportDocument(CriticalAccessQuery criticalAccessQuery, File file, Locale language) throws Exception {

        try (FileWriter csvWriter = new FileWriter(file)) {

            // prepare csv format
            char separator = language.equals(Locale.GERMAN) ? ';' : ',';

            String[] headers = language.equals(Locale.GERMAN)
                ? new String[] {"Verletztes Pattern", "Kritischer Benutzer", "Beschreibung", "Author: " + criticalAccessQuery.getCreatedBy(), "Erstellt am: " + criticalAccessQuery.getCreatedAt()}
                : new String[] {"Violated Pattern", "Critical Username", "Description", "Author: " + criticalAccessQuery.getCreatedBy(), "Created at: " + criticalAccessQuery.getCreatedAt()};

            CSVFormat format = CSVFormat.DEFAULT.withDelimiter(separator).withHeader(headers);

            // open csv output stream writer
            try (CSVPrinter csvPrinter = new CSVPrinter(csvWriter, format)) {

                // write critical access entries to file
                for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
                    csvPrinter.printRecord(criticalAccessEntry.getAccessPattern().getUsecaseId(), criticalAccessEntry.getUsername(), criticalAccessEntry.getAccessPattern().getDescription());
                }

                // make sure the output stream gets flushed, so everything is written to the output file
                csvPrinter.flush();
            }
        }
    }

}