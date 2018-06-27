package io.msoffice.word;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

public class ReportExportHelper implements IReportExportHelper {

    /**
     * This method exports the given critical access query to the given file path as word or pdf format.
     *
     * @param query    the critical access query to export
     * @param filePath the destination file path of the report
     * @param type     the file type of the report
     * @throws Exception caused by errors during file access etc.
     */

    @Override
    public void exportReport(CriticalAccessQuery query, String filePath, ReportExportType type) throws Exception {

        // load word template as resource
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("word-templates/ExportWord_template.docx")) {

            // init word document with template from resources
            try (XWPFDocument document = new XWPFDocument(stream)) {

                // write tables
                List<CriticalAccessEntry> sortedEntries =
                    query.getEntries().stream().sorted((x, y) -> {

                        // compare by username
                        int cmp = x.getUsername().compareTo(y.getUsername());

                        // if usernames match compare by usecase id of the violated access pattern
                        cmp = cmp != 0 ? cmp : x.getAccessPattern().getUsecaseId().compareTo(y.getAccessPattern().getUsecaseId());

                        return cmp;

                    }).collect(Collectors.toList());

                writeCriticalAccessEntriesToDocument(document, sortedEntries);
                //writeWhitelistToDocument(document, query.getConfig().getWhitelist());
                //writeAccessPatternsToDocument(document);

                // replace bookmarks
                // replaceBookmark(document, "CriticalQueryID", query.getId().toString());
                replaceBookmark(document, "ServerDestination", query.getSapConfig().getServerDestination());
                replaceBookmark(document, "SysNr", query.getSapConfig().getSysNr());
                replaceBookmark(document, "Client", query.getSapConfig().getClient());
                replaceBookmark(document, "Language", query.getSapConfig().getLanguage());
                replaceBookmark(document, "PoolCapacity", query.getSapConfig().getPoolCapacity());
                replaceBookmark(document, "CreatedBy", query.getSapConfig().getCreatedBy());
                replaceBookmark(document, "CreatedAt", getDateAsString());
                replaceBookmark(document, "SapDescription", query.getSapConfig().getDescription());

                // replaceBookmark(document, "CreatedAt", );

                // write Blog chart
                BlockChart chart = new BlockChart();
                chart.writeChartToDocumentAccesPattern(document, sortedEntries);
                chart.writeChartToDocumentUser(document, sortedEntries);


                // write results to file
                try (FileOutputStream out = new FileOutputStream(new File(filePath))) {

                    if (type == ReportExportType.Word) {

                        document.write(out);

                    } else if (type == ReportExportType.Pdf) {

                        PdfOptions options = PdfOptions.create().fontEncoding("windows-1250");
                        PdfConverter.getInstance().convert(document, out, options);

                    } else {
                        throw new IllegalArgumentException("unknown export type");
                    }
                }

                System.out.println("report successully written");
            }
        }
    }

    public String getDateAsString() {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(new Date());
    }

    private void writeCriticalAccessEntriesToDocument(XWPFDocument document, List<CriticalAccessEntry> entries) throws Exception {

        // write critical access entries
        XWPFTable criticalAccessEntriesTable = document.getTables().get(0);


        for (CriticalAccessEntry entry : entries) {
            XWPFTableRow row = criticalAccessEntriesTable.getRow(1);
            CTRow ctRow = CTRow.Factory.newInstance();
            ctRow.set(row.getCtRow());
            XWPFTableRow row2 = new XWPFTableRow(ctRow, criticalAccessEntriesTable);
            row2.getCell(0).setText(entry.getUsername());
            System.out.println(entry.getUsername());
            row2.getCell(1).setText(entry.getAccessPattern().getUsecaseId());
            criticalAccessEntriesTable.addRow(row2);
        }

        criticalAccessEntriesTable.removeRow(1);
    }

    private void writeWhitelistToDocument(XWPFDocument document, Whitelist whitelist) throws Exception {
        /**
         // write whitelist
         XWPFTable whitelistTable = document.getTables().get(1);

         for (WhitelistEntry user : whitelist.getEntries()) {

         XWPFTableRow row = whitelistTable.getRow(1);
         CTRow ctRow = CTRow.Factory.newInstance();
         ctRow.set(row.getCtRow());
         XWPFTableRow row2 = new XWPFTableRow(ctRow, whitelistTable);
         XWPFTableRow row3 = new XWPFTableRow(ctRow, whitelistTable);
         row2.getCell(0).setText(user.getUsername());
         row3.getCell(1).setText(user.getUsecaseId());
         whitelistTable.addRow(row2);
         }

         whitelistTable.removeRow(1);
         */
    }

    private void writeAccessPatternsToDocument(XWPFDocument document) throws Exception {

        // TODO: exporting the patterns is much more complicated. leave this part out and only export the patterns as excel file (logic already exists)

            /*
            //  write access patterns
            XWPFTable patternsTable = document.getTables().get(2);
            List<AccessCondition> condition = new ArrayList<>(criticalaccessquery.getEntries().iterator().next().getAccessPattern().getConditions());
            //System.out.println(condition);
            AccessPatternCondition PatternCondition = condition.iterator().next().getPatternCondition();

            for (AccessPatternConditionProperty property : PatternCondition.getProperties()) {

                XWPFTableRow row = table.getRow(1);
                CTRow ctRow = CTRow.Factory.newInstance();
                ctRow.set(row.getCtRow());
                XWPFTableRow row2 = new XWPFTableRow(ctRow, table);

                row2.getCell(1).setText(property.getAuthObject());
                row2.getCell(2).setText(property.getValue1());
                row2.getCell(3).setText(property.getValue2());
                row2.getCell(4).setText(property.getValue3());
                row2.getCell(5).setText(property.getValue4());

                table.addRow(row2);
            }

            table.removeRow(1);
            */
    }

    private void replaceBookmark(XWPFDocument document, String key, String value) {

        List<XWPFParagraph> paragraphs = document.getParagraphs();

        for (XWPFParagraph paragraph : paragraphs) {

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
