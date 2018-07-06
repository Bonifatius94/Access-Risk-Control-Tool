package io.msoffice.word;

import data.entities.CriticalAccessQuery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public class PdfExportHelper extends ReportExportHelperBase {

    /**
     * This method exports a critical access query to the given file with the given export format.
     *
     * @param query is the result of an anlysis
     * @param file is the File, where the critical entries are written in
     * @param language is the language of the export format
     * @throws Exception caused by incompatible file type or failed write process and other I/O exceptions
     */
    @Override
    public void exportDocument(CriticalAccessQuery query, File file, Locale language) throws Exception {

        // init word document with template from resources
        try (XWPFDocument document = prepareDocument(query, language)) {

            // write results to file
            try (FileOutputStream out = new FileOutputStream(file)) {

                // write document as pdf
                // TODO: check if this also works on linux
                PdfOptions options = PdfOptions.create().fontEncoding("windows-1250");
                PdfConverter.getInstance().convert(document, out, options);
            }
        }
    }

}
