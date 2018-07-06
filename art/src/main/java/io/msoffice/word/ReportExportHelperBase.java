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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;

import settings.UserSettingsHelper;


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

    protected XWPFDocument prepareDocument(CriticalAccessQuery query, Locale language) throws Exception {

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

        // only add the charts if there is more than one usecase
        if (query.getConfig().getPatterns().size() > 1) {
            writeChartsToDocument(document, query, language);
        }

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

        XWPFTable originalTable = document.getTables().get(2);
        boolean isTablePageBreakRequired = sortedEntries.size() > 21;

        // manage the page break manually because the pdf export does not support the table page break feature from MS Word
        if (isTablePageBreakRequired) {

            // retrieve the remaining entries (without entries from first page)
            List<CriticalAccessEntry> remainingEntries = new ArrayList<>(sortedEntries.subList(21, sortedEntries.size()));

            while (remainingEntries.size() > 0) {

                // start a new page and clone the table there
                XWPFTable clone = cloneTableAndWriteItToNewPage(document, originalTable);

                // retrieve the remaining entries and write them to the cloned table
                int itemsCount = remainingEntries.size() > 48 ? 48 : remainingEntries.size();
                List<CriticalAccessEntry> entriesToWrite = new ArrayList<>(remainingEntries.subList(0, itemsCount));
                writeEntriesToTable(clone, entriesToWrite);
                remainingEntries.removeAll(entriesToWrite);
            }
        }

        // write all entries to the table on the first page
        writeEntriesToTable(originalTable, isTablePageBreakRequired ? sortedEntries.subList(0, 21) : sortedEntries);
    }

    private XWPFTable cloneTableAndWriteItToNewPage(XWPFDocument document, XWPFTable table) {

        // insert page break into document
        document.createParagraph().setPageBreak(true);

        // clone the existing table
        CTTbl tbl = document.getDocument().getBody().insertNewTbl(document.getTables().size());
        tbl.set(table.getCTTbl());
        XWPFTable clone = new XWPFTable(tbl, document);

        return clone;
    }

    private void writeEntriesToTable(XWPFTable table, List<CriticalAccessEntry> entries) {

        final int dataTemplateRowIndex = 2;
        XWPFTableRow dataTemplateRow = table.getRow(dataTemplateRowIndex);

        // write critical access entries
        for (CriticalAccessEntry entry : entries) {

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

        table.removeRow(dataTemplateRowIndex);
    }

    private void writeChartsToDocument(XWPFDocument document, CriticalAccessQuery query, Locale language) throws Exception {

        for (BufferedImage image : getExportedCharts(query, language)) {

            if (image != null) {

                // start a new page and insert the chart there
                document.createParagraph().setPageBreak(true);
                writeImageToDocument(document, image);
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
        int newWidth = (int) ((double) image.getWidth() / ((double) 4 / 3));
        int newHeight = (int) ((double) image.getHeight() / ((double) 4 / 3));

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

    /**
     * Returns the charts to export as a list of BufferedImages.
     */
    public List<BufferedImage> getExportedCharts(CriticalAccessQuery query, Locale language) throws Exception {

        List<BufferedImage> images = new ArrayList<>();

        images.add(exportUsecaseChart(query, language));
        images.add(exportUsernameChart(query, language));

        return images;
    }

    /**
     * Exports the UsecaseChart as a BufferedImage (snapshot).
     */
    @SuppressWarnings("all")
    private BufferedImage exportUsecaseChart(CriticalAccessQuery query, Locale language) throws Exception {

        // y axis properties
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setAutoRanging(false);
        numberAxis.setLowerBound(0);
        numberAxis.setTickUnit(5);
        numberAxis.setAnimated(false);

        // x axis properties
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setAutoRanging(true);
        categoryAxis.setAnimated(false);

        BarChart<String, Integer> chart = new BarChart(categoryAxis, numberAxis);

        chart.setLegendVisible(false);

        Map<String, Integer> itemsXCount;

        // group by usecase id
        itemsXCount =
            query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId())
                .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

        // get the correct string from the chosen locale
        ResourceBundle bundle = ResourceBundle.getBundle("lang", language);
        chart.setTitle(bundle.getString("usecaseIdViolations"));

        XYChart.Series<String, Integer> mainSeries = new XYChart.Series<>();

        // calculate maximum of entries for upper bound (round up to nearest 10)
        int maximum = itemsXCount.values().stream().max(Integer::compareTo).get();
        int upperBound = 5 * ((maximum + 4) / 5);
        numberAxis.setUpperBound(upperBound);

        int all = 0;
        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet()) {
            all += entry.getValue();
        }

        // compute average
        int average = all / itemsXCount.size();

        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())) {
            XYChart.Data<String, Integer> data = createData(entry.getKey(), entry.getValue(), average);

            mainSeries.getData().add(data);
        }

        // add the main series to the chart
        chart.getData().add(mainSeries);

        // cap max chart size
        chart.setMaxWidth(1000);

        return chartToBufferedImage(chart);
    }

    /**
     * Exports the UsernameChart as a BufferedImage (snapshot).
     */
    @SuppressWarnings("all")
    private BufferedImage exportUsernameChart(CriticalAccessQuery query, Locale language) throws Exception {

        // y axis properties
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setAutoRanging(false);
        numberAxis.setLowerBound(0);
        numberAxis.setTickUnit(5);
        numberAxis.setAnimated(false);

        // x axis properties
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setAutoRanging(true);
        categoryAxis.setAnimated(false);

        BarChart<Integer, String> chart = new BarChart(numberAxis, categoryAxis);

        chart.setLegendVisible(false);

        Map<String, Integer> itemsXCount;

        // group by username
        itemsXCount =
            query.getEntries().stream().map(x -> x.getUsername())
                .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

        // get the correct string from the chosen locale
        ResourceBundle bundle = ResourceBundle.getBundle("lang", language);
        chart.setTitle(bundle.getString("usernameViolations"));

        // calculate maximum of entries for upper bound (round up to nearest 10)
        int maximum = itemsXCount.values().stream().max(Integer::compareTo).get();
        int upperBound = 5 * ((maximum + 4) / 5);
        numberAxis.setUpperBound(upperBound);

        int all = 0;
        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet()) {
            all += entry.getValue();
        }

        XYChart.Series<Integer, String> mainSeries = new XYChart.Series<>();

        // compute average
        int average = all / itemsXCount.size();

        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())) {
            XYChart.Data<Integer, String> data = createDataInverted(entry.getKey(), entry.getValue(), average);

            mainSeries.getData().add(data);
        }

        // add the main series to the chart
        chart.getData().add(mainSeries);

        // cap max chart size
        chart.setPrefHeight(itemsXCount.size() * 40);
        chart.setMaxHeight(1000);
        chart.setMaxWidth(1100);

        return chartToBufferedImage(chart);
    }

    /**
     * Creates the data and adds a label with the value.
     */
    private XYChart.Data<String, Integer> createData(String key, int value, int average) {

        Label label = new Label("" + value);
        label.getStyleClass().add("bar-value");
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.BOTTOM_CENTER);
        StackPane.setMargin(group, new Insets(0, 0, 5, 0));

        StackPane node = new StackPane();
        node.getChildren().add(group);

        // color the nodes that are above the average
        if (value > average) {
            node.getStyleClass().add("warning-bar");
        }

        XYChart.Data<String, Integer> data = new XYChart.Data<>(key, value);
        data.setNode(node);

        return data;
    }

    /**
     * Creates the data and adds a label with the value.
     */
    private XYChart.Data<Integer, String> createDataInverted(String key, int value, int average) {

        Label label = new Label("" + value);
        label.getStyleClass().add("bar-value");
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.CENTER_RIGHT);
        StackPane.setMargin(group, new Insets(0, 5, 0, 0));

        StackPane node = new StackPane();
        node.getChildren().add(group);

        // color the nodes that are above the average
        if (value > average) {
            node.getStyleClass().add("warning-bar");
        }

        XYChart.Data<Integer, String> data = new XYChart.Data<>(value, key);
        data.setNode(node);

        return data;
    }

    /**
     * Makes a snapshot of the given chart and returns a BufferedImage of it.
     */
    private BufferedImage chartToBufferedImage(BarChart chart) throws Exception {
        Scene scene = new Scene(new AnchorPane(chart));

        // add styles to the scene
        scene.getRoot().setStyle(new UserSettingsHelper().loadUserSettings().getDarkThemeCss());
        scene.getStylesheets().add("css/bar-export.css");

        // add main-root to the root so tooltip styling is not affected
        scene.getRoot().getStyleClass().add("main-root");

        WritableImage image = scene.snapshot(null);

        return SwingFXUtils.fromFXImage(image, null);
    }
}
