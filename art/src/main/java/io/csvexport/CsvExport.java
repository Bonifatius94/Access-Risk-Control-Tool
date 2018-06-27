package io.csvexport;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvExport {


    /**
     * Starts Csv Export with an criticalAccessQuery.
     * @param criticalAccessQuery is the result of an anlysis
     * @param file is the File, where the critical entries are written in
     * @param separator is a chosen separator
     * @throws IOException caused by incompatible file type or failed write process and other I/O exceptions
     */
    public static void startCsvExport(CriticalAccessQuery criticalAccessQuery, File file, char separator) throws IOException {

        FileWriter csvWriter = new FileWriter(file);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withDelimiter(separator).withHeader("CriticalUserID", "CriticalUserName"));

        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getAccessPattern().getUsecaseId(), criticalAccessEntry.getUsername());
        }

        csvPrinter.flush();
    }

    /**
     * Starts Csv Export with an criticalAccessQuery.
     * @param criticalAccessQuery is the result of an anlysis
     * @param filepath is the filepath, of target file
     * @param separator is a chosen separator
     * @throws IOException caused by incompatible file type or failed write process and other I/O exceptions
     */
    public static void startCsvExport(CriticalAccessQuery criticalAccessQuery, String filepath, char separator) throws IOException {

        FileWriter csvWriter = new FileWriter(filepath);
        CSVPrinter csvPrinter;
        csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withDelimiter(separator).withHeader("CriticalUserID", "CriticalUserName"));

        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getAccessPattern().getUsecaseId(), criticalAccessEntry.getUsername());
        }
        csvPrinter.flush();

    }

    /**
     * Starts Csv Export with an criticalAccessQuery. Separator is by default a comma
     * @param criticalAccessQuery is the result of an anlysis
     * @param filepath is the filepath, of the target file
     * @throws IOException caused by incompatible file type or failed write process and other I/O exceptions
     */
    public static void startCsvExport(CriticalAccessQuery criticalAccessQuery, String filepath) throws IOException {

        FileWriter csvWriter = new FileWriter(filepath);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName"));


        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getAccessPattern().getUsecaseId(), criticalAccessEntry.getUsername());
        }
        csvPrinter.flush();
    }

    /**
     * Starts Csv Export with an criticalAccessQuery.
     * @param criticalAccessQuery is the result of an anlysis
     * @param file is the File, where the critical entries are written in
     * @throws IOException caused by incompatible file type or failed write process and other I/O exceptions
     */
    public static void startCsvExport(CriticalAccessQuery criticalAccessQuery, File file) throws IOException {

        FileWriter csvWriter = new FileWriter(file);
        CSVPrinter csvPrinter;
        csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName"));

        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getAccessPattern().getUsecaseId(), criticalAccessEntry.getUsername());
        }
        csvPrinter.flush();

    }

}