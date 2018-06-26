package io.csvexport;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvExport {


    /**
     * Starts Csv Export with an criticalAccessQuery.
     *
     * @param criticalAccessQuery is the result of an anlysis
     * @throws IOException caused by wrong filename or
     */
    public static void startCsvExport(CriticalAccessQuery criticalAccessQuery) throws IOException {
        //TODO: richtiger export implementierung bzw. wie kann mann das mit ner auswahl machen ???

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???*/);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName"));
        csvPrinter.printRecord("stuff", "stuff");


        //TODO: real print funktion Needs to be Tested

        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername());
        }

        csvPrinter.flush();

    }

    /**
     * Starts Csv Export with an criticalAccessQuery.
     *
     * @param criticalAccessQuery the result of an analysis
     * @param whitelistId         is the given id of an Whitelist which was used by the analysis.
     * @throws IOException if an error while file writing occurred
     */
    public static void startCsvExport(CriticalAccessQuery criticalAccessQuery, int whitelistId) throws IOException {
        //TODO: implement

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???*/);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName", "WhitelistID"));

        //TODO: real print funktion Needs to be Tested

        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername(), whitelistId);
        }

        csvPrinter.flush();

    }
    /*
    /**
     * @param criticalAccessQuery
     * @param whitelistID
     * @param accessPatternID
     * @throws IOException
     *
    public static void StartCsvExport(CriticalAccessQuery criticalAccessQuery, int whitelistID, int accessPatternID) throws IOException {

        //TODO: implement

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName", "WhitelistID", "AccessPatternID"));

        //TODO: real print funktion Needs to be Tested

        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername(), whitelistID, accessPatternID);
        }

        csvPrinter.flush();
    }

    /**
     * @param criticalAccessQuery
     * @param whitelistID
     * @param accessPatternId
     * @param configurationID
     * @throws IOException
     *
    public static void startCsvExport(CriticalAccessQuery criticalAccessQuery, int whitelistID, int accessPatternId, int configurationID) throws IOException {
        //TODO: implement

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???*);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName", "WhitelistID", "AccessPatternID", "ConfigurationID"));

        //TODO: real print funktion Needs to be Tested

        for (CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername(), whitelistID, configurationID);
        }

        csvPrinter.flush();
    }*/
}