package io.csvexport;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import javafx.stage.FileChooser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;


public class CSVExport {


    /**
     *
     * @param criticalAccessQuery
     * @throws IOException
     */
    public static void StartCsvExport(CriticalAccessQuery criticalAccessQuery) throws IOException {
        //TODO: richtiger export implementierung bzw. wie kann mann das mit ner auswahl machen ???

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???*/);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID","CriticalUserName"));
        csvPrinter.printRecord("stuff", "stuff");




        //TODO: real print funktion Needs to be Tested

        for(CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername());
        }

        csvPrinter.flush();


    }

    /**
     *
     * @param criticalAccessQuery
     * @param whitelistID
     * @throws IOException
     */
    public static void StartCsvExport(CriticalAccessQuery criticalAccessQuery, int whitelistID) throws IOException {
        //TODO: implement

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???*/);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName", "WhitelistID"));

        //TODO: real print funktion Needs to be Tested

        for(CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername(), whitelistID);
        }

        csvPrinter.flush();

    }

    /**
     *
     * @param criticalAccessQuery
     * @param whitelistID
     * @param accessPatternID
     * @throws IOException
     */
    public static void StartCsvExport(CriticalAccessQuery criticalAccessQuery, int whitelistID, int accessPatternID) throws IOException {

        //TODO: implement

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???*/);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName", "WhitelistID", "AccessPatternID"));

        //TODO: real print funktion Needs to be Tested

        for(CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername(), whitelistID, accessPatternID);
        }

        csvPrinter.flush();
    }

    /**
     *
     * @param criticalAccessQuery
     * @param whitelistID
     * @param accessPatternID
     * @param configurationID
     * @throws IOException
     */
    public static void StartCsvExport(CriticalAccessQuery criticalAccessQuery, int whitelistID, int accessPatternID, int configurationID) throws IOException {
        //TODO: implement

        FileWriter csvWriter = new FileWriter("example.csv"/* TODO: woher kommt der File Name ???*/);
        CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("CriticalUserID", "CriticalUserName", "WhitelistID","AccessPatternID", "ConfigurationID"));

        //TODO: real print funktion Needs to be Tested

        for(CriticalAccessEntry criticalAccessEntry : criticalAccessQuery.getEntries()) {
            csvPrinter.printRecord(criticalAccessEntry.getId(), criticalAccessEntry.getUsername(), whitelistID, configurationID);
        }

        csvPrinter.flush();
    }
}