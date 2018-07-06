package io.msoffice;

import data.entities.CriticalAccessQuery;

import io.msoffice.csv.CsvExportHelper;
import io.msoffice.word.PdfExportHelper;
import io.msoffice.word.WordExportHelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Locale;

public class ExportHelper {

    /**
     * This method exports a critical access query to the given file with the given export format.
     *
     * @param query is the result of an anlysis
     * @param file is the File, where the critical entries are written in
     * @param type is the type of the output format
     * @param language is the language of the export format
     * @throws Exception caused by incompatible file type or failed write process and other I/O exceptions
     */
    public void exportDocument(CriticalAccessQuery query, File file, ReportExportType type, Locale language) throws Exception {

        // get export helper and export the document accordingly
        getExportHelper(type).exportDocument(query, file, language);
    }

    private IReportExportHelper getExportHelper(ReportExportType type) {

        switch (type) {
            case Csv:  return new CsvExportHelper();
            case Pdf:  return new PdfExportHelper();
            case Word: return new WordExportHelper();
            default:   throw new IllegalArgumentException("unknown export type");
        }
    }

}
