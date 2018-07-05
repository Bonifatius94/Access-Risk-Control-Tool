package io.msoffice.word;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import io.msoffice.IReportExportHelper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;


public abstract class ReportExportHelperBase implements IReportExportHelper {

    // TODO: check if this also works on english machines etc.
    private static final Font STANDARD_FONT = new Font("Calibri (Textkörper)", 0, 11);

    protected XWPFDocument prepareDocument(CriticalAccessQuery query, Locale language, List<BufferedImage> chartImages) throws Exception {

        XWPFDocument document;
        String templatePath = language.equals(Locale.GERMAN) ? "word-templates/ExportReport_Template_DE.docx" : "word-templates/ExportReport_Template_EN.docx";

        // load word template as resource
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(templatePath)) {

            // init word document with template from resources
            document = new XWPFDocument(stream);

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

            // replace bookmarks
            replaceBookmark(document, "ServerDestination", query.getSapConfig().getServerDestination());
            replaceBookmark(document, "SysNr", query.getSapConfig().getSysNr());
            replaceBookmark(document, "Client", query.getSapConfig().getClient());
            replaceBookmark(document, "Language", query.getSapConfig().getLanguage());
            replaceBookmark(document, "PoolCapacity", query.getSapConfig().getPoolCapacity());
            replaceBookmark(document, "CreatedBy", query.getCreatedBy());
            replaceBookmark(document, "CreatedAt", getDateAsString(query.getCreatedAt(), language));
            replaceBookmark(document, "SapDescription", query.getSapConfig().getDescription());

            // add the charts to the document
            if (chartImages != null) {
                for (BufferedImage image : chartImages) {
                    if (image != null) {
                        document.createParagraph().setPageBreak(true);
                        writeImageToDocument(document, image);
                    }
                }
            }
        }

        return document;
    }

    private void writeImageToDocument(XWPFDocument document, BufferedImage image) throws Exception {

        // get image as stream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream chartInputStream = new ByteArrayInputStream(os.toByteArray());

        // write image to document
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addPicture(chartInputStream, document.PICTURE_TYPE_PNG, null, Units.toEMU(image.getWidth()), Units.toEMU(image.getHeight()));
    }

    private String getDateAsString(ZonedDateTime dateTime, Locale language) {

        //Locale country = language.equals(Locale.GERMAN) ? Locale.GERMANY : Locale.US;
        Date date = Date.from(dateTime.toLocalDate().atStartOfDay(ZoneId.of("UTC")).toInstant());
        return DateFormat.getDateInstance(DateFormat.MEDIUM, language).format(date);
    }

    private void writeCriticalAccessEntriesToDocument(XWPFDocument document, java.util.List<CriticalAccessEntry> entries) {

        // write critical access entries
        XWPFTable table = document.getTables().get(0);

        for (CriticalAccessEntry entry : entries) {

            // clone empty data template row
            XWPFTableRow row = table.getRow(1);
            CTRow ctRow = CTRow.Factory.newInstance();
            ctRow.set(row.getCtRow());
            XWPFTableRow row2 = new XWPFTableRow(ctRow, table);

            // set content of cloned data row
            row2.getCell(0).setText(entry.getUsername());
            row2.getCell(1).setText(entry.getAccessPattern().getUsecaseId());

            // apply data row to table
            table.addRow(row2);
        }

        // remove empty data template row
        table.removeRow(1);
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
