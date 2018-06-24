package io.msoffice.excel.ResultWordExport;

import data.entities.CriticalAccessEntry;
import data.entities.Whitelist;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ExportWord extends SaveResult {

    public ExportWord(String filePath_tempplate, String filePath, CriticalAccessEntry criticalaccessentry, Whitelist whitelist) throws Exception {

        FileInputStream fis = null;

        Set<String> bulletPoints = new HashSet<>();
        File file = new File(filePath_tempplate);
        fis = new FileInputStream(file.getAbsolutePath());
        XWPFDocument document = new XWPFDocument(fis);

        List<XWPFParagraph> paragraphs = document.getParagraphs();

        for (XWPFParagraph paragraph : paragraphs) {
            //Here you have your paragraph;
            CTP ctp = paragraph.getCTP();
            // Get all bookmarks and loop through them
            List<CTBookmark> bookmarks = ctp.getBookmarkStartList();
            for (CTBookmark bookmark : bookmarks) {
                if (bookmark.getName().equals("CriticalUserName")) {
                    // System.out.println(bookmark.getName());
                    XWPFRun run = paragraph.createRun();
                    run.setText(criticalaccessentry.getUsername());
                    ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                }
                if (bookmark.getName().equals("AccessPatternID")) {
                    // System.out.println(bookmark.getName());
                    XWPFRun run = paragraph.createRun();
                    run.setText(criticalaccessentry.getId().toString());
                    ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
                }
            }
        }


        CreatePieChart(250, 250, 10, 20, 30, 40);

        XWPFParagraph paragraph;
        XWPFRun run;
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        FileInputStream fis1 = null;

        File file2 = new File("myimage.png");
        fis1 = new FileInputStream(file2.getAbsolutePath());

        run.addPicture(fis1, document.PICTURE_TYPE_PNG, "myimage.png", Units.toEMU(250), Units.toEMU(250));

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
