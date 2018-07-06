package io.msoffice.word;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.Whitelist;

import io.msoffice.IReportExportHelper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;


public abstract class ReportExportHelperBase implements IReportExportHelper {

    // =========================================
    //                CONSTANTS
    // =========================================

    private static final String BOOKMARK_QUERY_SETTINGS_CREATOR = "QuerySettings_Creator";
    private static final String BOOKMARK_QUERY_SETTINGS_END_OF_QUERY = "QuerySettings_EndOfQuery";
    private static final String BOOKMARK_QUERY_SETTINGS_PATTERNS = "QuerySettings_Patterns";
    private static final String BOOKMARK_QUERY_SETTINGS_WHITELIST = "QuerySettings_Whitelist";

    private static final String BOOKMARK_CONNECTION_SETTINGS_DESCRIPTION = "ConnectionSettings_Description";
    private static final String BOOKMARK_CONNECTION_SETTINGS_SERVER_DESTINATION = "ConnectionSettings_ServerDestination";
    private static final String BOOKMARK_CONNECTION_SETTINGS_SYSTEM_NUMBER = "ConnectionSettings_SystemNumber";
    private static final String BOOKMARK_CONNECTION_SETTINGS_CLIENT = "ConnectionSettings_Client";
    private static final String BOOKMARK_CONNECTION_SETTINGS_LANGUAGE = "ConnectionSettings_Language";
    private static final String BOOKMARK_CONNECTION_SETTINGS_POOL_CAPACITY = "ConnectionSettings_PoolCapacity";

    // =========================================
    //                 METHODS
    // =========================================

    protected XWPFDocument prepareDocument(CriticalAccessQuery query, Locale language, List<BufferedImage> chartImages) throws Exception {

        XWPFDocument document;

        // determine the resource path of the word template according to the selected report language
        String templatePath = language.equals(Locale.GERMAN) ? "word-templates/ExportReport_Template_DE.docx" : "word-templates/ExportReport_Template_EN.docx";

        // load the word template as a resource
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(templatePath)) {

            // init the word document with the template from resources
            document = new XWPFDocument(stream);
        }

        // write bookmark content
        writeBookmarkContentToDocument(document, query, language);

        // write the critical access entries table
        writeCriticalAccessEntriesToDocument(document, query);

        // write the overloaded charts to the document
        writeChartsToDocument(document, chartImages);

        return document;
    }

    private void writeBookmarkContentToDocument(XWPFDocument document, CriticalAccessQuery query, Locale language) {

        // format usecase ids of patterns
        final String patternsContent =
            query.getConfig().getPatterns().stream()
            .map(x -> x.getUsecaseId()).distinct().sorted()
            .collect(Collectors.joining(", "));

        // format whitelist name
        final Whitelist whitelist = query.getConfig().getWhitelist();
        final String whitelistNoneContent = language == Locale.GERMAN ? "keine" : "none";
        final String whitelistContent = whitelist != null ? whitelist.getName() + " - " + whitelist.getDescription() : whitelistNoneContent;

        // replace bookmarks of query settings
        replaceBookmark(document, BOOKMARK_QUERY_SETTINGS_CREATOR, query.getCreatedBy());
        replaceBookmark(document, BOOKMARK_QUERY_SETTINGS_END_OF_QUERY, getDateTimeAsString(query.getCreatedAt(), language));
        replaceBookmark(document, BOOKMARK_QUERY_SETTINGS_PATTERNS, patternsContent);
        replaceBookmark(document, BOOKMARK_QUERY_SETTINGS_WHITELIST, whitelistContent);

        // replace bookmarks of sap connection settings
        replaceBookmark(document, BOOKMARK_CONNECTION_SETTINGS_DESCRIPTION, query.getSapConfig().getDescription());
        replaceBookmark(document, BOOKMARK_CONNECTION_SETTINGS_SERVER_DESTINATION, query.getSapConfig().getServerDestination());
        replaceBookmark(document, BOOKMARK_CONNECTION_SETTINGS_SYSTEM_NUMBER, query.getSapConfig().getSysNr());
        replaceBookmark(document, BOOKMARK_CONNECTION_SETTINGS_CLIENT, query.getSapConfig().getClient());
        replaceBookmark(document, BOOKMARK_CONNECTION_SETTINGS_LANGUAGE, query.getSapConfig().getLanguage());
        replaceBookmark(document, BOOKMARK_CONNECTION_SETTINGS_POOL_CAPACITY, query.getSapConfig().getPoolCapacity());
    }

    private void writeCriticalAccessEntriesToDocument(XWPFDocument document, CriticalAccessQuery query) {

        // prepare the critical access entries (sorting by usecase id, then by critical username)
        List<CriticalAccessEntry> sortedEntries =
            query.getEntries().stream()
            .sorted(
                Comparator.comparing((CriticalAccessEntry x) -> x.getAccessPattern().getUsecaseId())
                .thenComparing(CriticalAccessEntry::getUsername)
            )
            .collect(Collectors.toList());

        // write critical access entries
        XWPFTable table = document.getTables().get(2);
        XWPFTableRow dataTemplateRow = table.getRow(2);

        for (CriticalAccessEntry entry : sortedEntries) {

            // clone empty data template row
            CTRow ctRow = CTRow.Factory.newInstance();
            ctRow.set(dataTemplateRow.getCtRow());
            XWPFTableRow dataRow = new XWPFTableRow(ctRow, table);

            // set content of cloned data row
            writeTextToCell(dataRow.getCell(0), entry.getAccessPattern().getUsecaseId());
            writeTextToCell(dataRow.getCell(1), entry.getUsername());

            // apply data row to table at the bottom
            table.addRow(dataRow);
        }

        // remove empty data template row
        table.removeRow(2);
    }

    private void writeChartsToDocument(XWPFDocument document, List<BufferedImage> chartImages) throws Exception {

        if (chartImages != null) {

            for (BufferedImage image : chartImages) {

                if (image != null) {

                    // start a new page and insert the chart there
                    document.createParagraph().setPageBreak(true);
                    writeImageToDocument(document, image);
                }
            }
        }
    }

    private void writeImageToDocument(XWPFDocument document, BufferedImage image) throws Exception {

        // get image as stream
        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageOut);
        InputStream imageIn = new ByteArrayInputStream(imageOut.toByteArray());

        // write image to document
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();

        // TODO: make the resize fill the page correctly

        // calculate the width / height of the image (image has 133% scaling factor, but no idea why ...)
        int newWidth = (int)((double)image.getWidth() / ((double)4 / 3));
        int newHeight = (int)((double)image.getHeight() / ((double)4 / 3));

        run.addPicture(imageIn, document.PICTURE_TYPE_PNG, null, Units.toEMU(newWidth), Units.toEMU(newHeight));
    }

    // =========================================
    //                  HELPERS
    // =========================================

    private String getDateTimeAsString(ZonedDateTime dateTime, Locale language) {

        // source: https://docs.oracle.com/javase/tutorial/i18n/format/dateFormat.html

        Date date = Date.from(Instant.from(dateTime));

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, language);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.MEDIUM, language);

        return dateFormatter.format(date) + " " + timeFormatter.format(date);
    }

    private void replaceBookmark(XWPFDocument document, String key, String value) {

        // replace bookmarks in plain text
        for (XWPFParagraph paragraph : document.getParagraphs()) {

            CTP ctp = paragraph.getCTP();
            List<CTBookmark> bookmarks = ctp.getBookmarkStartList();

            for (CTBookmark bookmark : bookmarks) {

                if (bookmark.getName().equals(key)) {

                    XWPFRun run = paragraph.createRun();
                    run.setText(value);
                    ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                }
            }
        }

        // replace bookmarks in tables
        for (XWPFTable table : document.getTables()) {

            for (XWPFTableRow row : table.getRows()) {

                for (XWPFTableCell cell : row.getTableCells()) {

                    for (XWPFParagraph paragraph : cell.getParagraphs()) {

                        CTP ctp = paragraph.getCTP();
                        List<CTBookmark> bookmarks = ctp.getBookmarkStartList();

                        for (CTBookmark bookmark : bookmarks) {

                            if (bookmark.getName().equals(key)) {

                                XWPFRun run = paragraph.createRun();
                                run.setText(value);
                                ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                            }
                        }
                    }
                }
            }
        }
    }

    private void writeTextToCell(XWPFTableCell cell, String text) {

        // this code is required due to pdf conversion issues when using the more standard cell.setText() method

        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }
}
