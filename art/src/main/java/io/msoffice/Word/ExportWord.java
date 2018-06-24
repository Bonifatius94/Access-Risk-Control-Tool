package io.msoffice.Word;

import data.entities.AccessCondition;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.Whitelist;

import data.entities.WhitelistEntry;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ExportWord extends SaveResult {

    public ExportWord(String filePath_tempplate, String filePath, CriticalAccessQuery criticalaccessquery, Whitelist whitelist) throws Exception {

        FileInputStream fis = null;

        Set<String> bulletPoints = new HashSet<>();
        File file = new File(filePath_tempplate);
        fis = new FileInputStream(file.getAbsolutePath());
        XWPFDocument document = new XWPFDocument(fis);

        List<XWPFTable> tables = document.getTables();
        int i = 1;

        for (XWPFTable table : tables) {

            if (i == 1) {

                // write white list

                for (WhitelistEntry user : whitelist.getEntries()) {

                    XWPFTableRow row = table.getRow(1);
                    CTRow ctRow = CTRow.Factory.newInstance();
                    ctRow.set(row.getCtRow());
                    XWPFTableRow row2 = new XWPFTableRow(ctRow, table);
                    XWPFTableRow row3 = new XWPFTableRow(ctRow, table);
                    row2.getCell(0).setText(user.getUsername());
                    row3.getCell(1).setText(user.getUsecaseId());
                    table.addRow(row2);
                }
                table.removeRow(1);
            }

            if (i == 2) {

                // write Critical User

                XWPFTableRow row = table.getRow(1);

                CTRow ctRow = CTRow.Factory.newInstance();
                ctRow.set(row.getCtRow());
                XWPFTableRow row2 = new XWPFTableRow(ctRow, table);
                row2.getCell(0).setText(criticalaccessquery.getEntries().iterator().next().getUsername());
                row2.getCell(1).setText(criticalaccessquery.getEntries().iterator().next().getId().toString());
                //table.removeRow(1);
                table.addRow(row2);
                table.removeRow(1);
            }

            if (i == 3) {

                //  write Access Pattern

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

            }

            i++;
        }

        // Replace Bookmarks
        //  --------------------------------------------------------------------------------------------------

        List<XWPFParagraph> paragraphs = document.getParagraphs();

        for (XWPFParagraph paragraph : paragraphs) {
            //Here you have your paragraph;
            CTP ctp = paragraph.getCTP();
            // Get all bookmarks and loop through them
            List<CTBookmark> bookmarks = ctp.getBookmarkStartList();
            for (CTBookmark bookmark : bookmarks) {
                XWPFRun run = paragraph.createRun();
                switch (bookmark.getName()) {
                    case "CriticalQueryID":

                        run.setText(criticalaccessquery.getId().toString());
                        ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                        break;

                    case "ServerDestination":
                        run.setText(criticalaccessquery.getSapConfig().getServerDestination());
                        ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                        break;

                    case "SysNr":
                        run.setText(criticalaccessquery.getSapConfig().getSysNr());
                        ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                        break;

                    case "Client":
                        run.setText(criticalaccessquery.getSapConfig().getClient());
                        ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                        break;

                    case "Language":
                        run.setText(criticalaccessquery.getSapConfig().getLanguage());
                        ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                        break;

                    case "PoolCapacity":
                        run.setText(criticalaccessquery.getSapConfig().getPoolCapacity());
                        ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                        break;
                }

            }
        }

        //  Pie Chart
        //  --------------------------------------------------------------------------------------------------
        CreatePieChart(250, 250, 10, 20, 30, 40);

        XWPFParagraph paragraph;
        XWPFRun run;
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        FileInputStream fis1 = null;

        File file2 = new File("myimage.png");
        fis1 = new FileInputStream(file2.getAbsolutePath());

        run.addPicture(fis1, document.PICTURE_TYPE_PNG, "myimage.png", Units.toEMU(250), Units.toEMU(250));

        // Write file
        //  --------------------------------------------------------------------------------------------------

        FileOutputStream out = new FileOutputStream(new File(filePath));
        document.write(out);
        out.close();
        System.out.println("createdocument.docx written successully");

    }

    public void CreatePieChart(int width, int height, int slice1, int slice2, int slice3, int slice4) throws Exception {


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

        // Save as PNG
        File file1 = new File("myimage.png");
        ImageIO.write(bufferedImage, "png", file1);

    }
}
