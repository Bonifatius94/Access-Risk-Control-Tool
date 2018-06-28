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

    protected XWPFDocument prepareDocument(CriticalAccessQuery query, Locale language) throws Exception {

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

            // write Blog chart
            document.createParagraph().setPageBreak(true);
            writeImageToDocument(document, getBlockChart(sortedEntries, BlockChartType.AccessPattern));
            writeImageToDocument(document, getBlockChart(sortedEntries, BlockChartType.User));
        }

        return document;
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

    private enum BlockChartType {
        AccessPattern,
        User
    }

    private BufferedImage getBlockChart(List<CriticalAccessEntry> entries, BlockChartType type) {

        // define width and height
        final int width = 450;
        final int height = 320;

        Map<String, Integer> itemsXCount;

        if (type == BlockChartType.AccessPattern) {

            // group by usecase id
            itemsXCount =
                entries.stream().map(x -> x.getAccessPattern().getUsecaseId())
                    .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

        } else if (type == BlockChartType.User) {

            // group by username
            itemsXCount =
                entries.stream().map(x -> x.getUsername())
                .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

        } else {
            throw new IllegalArgumentException("unknown block chart type");
        }

        return createBlockChart(itemsXCount, width, height, type);
    }

    private BufferedImage createBlockChart(Map<String, Integer> itemsXCount, int width, int height, BlockChartType type) {

        // TODO: create chart using apache poi cells: https://stackoverflow.com/questions/38913412/create-bar-chart-in-excel-with-apache-poi

        // construct a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // TODO: check if this call is obsolete
        g2d.setColor(Color.white);

        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.lightGray);

        int y = 270;

        // draw lines
        // TODO: why always 10 lines?
        for (int i = 1; i < 10; i++) {

            g2d.drawLine(40, y, 440, y);
            y = y - 27;
        }

        // determine the maximal number of users
        int maxUser = itemsXCount.values().stream().mapToInt(x -> x).max().getAsInt();

        // calculating y range per user
        // TODO: wouldn't double type fit better?!
        int yrange = 216 / maxUser;

        // draw charts + x text
        // TODO: wouldn't double type fit better?!
        int x = 400 / itemsXCount.size() / 2 + 40;

        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet()) {

            y = 54 + ((maxUser - entry.getValue()) * yrange);

            g2d.setColor(Color.red);
            g2d.fillRect(x, y, 10, (entry.getValue() * yrange));
            g2d.setColor(Color.darkGray);
            g2d.setFont(STANDARD_FONT);
            g2d.drawString(entry.getKey(), x, 290);

            x = x + 400 / itemsXCount.size();
        }

        // draw text for axis
        if (type == BlockChartType.AccessPattern) {

            g2d.drawString("Access", 390, 310);
            g2d.drawString("Pattern IDs", 390, 320);
            g2d.drawString("Numbers ", 0, 20);
            g2d.drawString("of violated ", 0, 30);
            g2d.drawString("users", 0, 40);

        } else if (type == BlockChartType.User) {

            g2d.drawString("User IDs", 403, 310);
            g2d.drawString("Numbers ", 0, 20);
            g2d.drawString("of violated ", 0, 30);
            g2d.drawString("Access Patterns", 0, 40);
        }

        //----------------------------------------------------------------------

        y = 270;
        x = 10;
        g2d.drawString(" " + 0, x, y);

        int yadd = 216 / 4;
        y = y - yadd;

        double result = (double)maxUser / 4;
        double result1 = result;

        // TODO: why always 5 iterations?
        for (int i = 0; i < 5; i++) {

            System.out.println("y " + y);
            System.out.println("result " + result);

            g2d.drawString(" " + result, x, y);
            y = y - yadd;
            result = result + result1;
        }

        return bufferedImage;
    }

}
