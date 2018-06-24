package io.msoffice.word;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

public class ReportExportHelper implements IReportExportHelper {

    /**
     * This method exports the given critical access query to the given file path as word or pdf format.
     *
     * @param query the critical access query to export
     * @param filePath the destination file path of the report
     * @param type the file type of the report
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
                //replaceBookmark(document, "CriticalQueryID", query.getId().toString());
                replaceBookmark(document, "ServerDestination", query.getSapConfig().getServerDestination());
                replaceBookmark(document, "SysNr", query.getSapConfig().getSysNr());
                replaceBookmark(document, "Client", query.getSapConfig().getClient());
                replaceBookmark(document, "Language", query.getSapConfig().getLanguage());
                replaceBookmark(document, "PoolCapacity", query.getSapConfig().getPoolCapacity());

                // write pie chart
                writeChartToDocument(document);

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

    private void writeCriticalAccessEntriesToDocument(XWPFDocument document, List<CriticalAccessEntry> entries) throws Exception {

        // write critical access entries
        XWPFTable criticalAccessEntriesTable = document.getTables().get(1);

        for (CriticalAccessEntry entry : entries) {

            XWPFTableRow row = criticalAccessEntriesTable.getRow(1);
            CTRow ctRow = CTRow.Factory.newInstance();
            ctRow.set(row.getCtRow());
            XWPFTableRow row2 = new XWPFTableRow(ctRow, criticalAccessEntriesTable);
            row2.getCell(0).setText(entry.getUsername());
            row2.getCell(1).setText(entry.getAccessPattern().getUsecaseId());
            criticalAccessEntriesTable.addRow(row2);
        }

        criticalAccessEntriesTable.removeRow(1);
    }

    private void writeWhitelistToDocument(XWPFDocument document, Whitelist whitelist) throws Exception {

        // write whitelist
        XWPFTable whitelistTable = document.getTables().get(0);

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

    private void writeChartToDocument(XWPFDocument document) throws Exception {

        // get image as stream
        BufferedImage chart = createPieChart(250, 250, 10, 20, 30, 40);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(chart,"png", os);
        InputStream chartInputStream = new ByteArrayInputStream(os.toByteArray());

        // write image to document
        XWPFParagraph paragraph;
        XWPFRun run;
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.addPicture(chartInputStream, document.PICTURE_TYPE_PNG, null, Units.toEMU(250), Units.toEMU(250));
    }

    private BufferedImage createPieChart(int width, int height, int slice1, int slice2, int slice3, int slice4) throws Exception {


        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // fill all the image with white
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        // create a circle with black
        // g2d.setColor(Color.black);
        // g2d.fillOval(0, 0, width, height);

        slice1 = (int) (slice1 * 3.6);
        slice2 = (int) (slice2 * 3.6);
        slice3 = (int) (slice3 * 3.6);
        slice4 = (int) (slice4 * 3.6);

        g2d.setColor(Color.green);
        g2d.fillArc(0, 0, width, height, 0, slice1);
        g2d.setColor(Color.CYAN);
        g2d.fillArc(0, 0, width, height, slice1, slice2);
        g2d.setColor(Color.BLUE);
        g2d.fillArc(0, 0, width, height, slice1 + slice2, slice3);
        g2d.setColor(Color.MAGENTA);
        g2d.fillArc(0, 0, width, height, slice1 + slice2 + slice3, slice4);

        // create a string with yellow
        g2d.setColor(Color.yellow);
        g2d.drawString("Test Text", 50, 120);

        // Disposes of this graphics context and releases any system resources that it is using.
        g2d.dispose();

        return bufferedImage;
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
