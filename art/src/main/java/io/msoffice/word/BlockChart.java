package io.msoffice.word;

import data.entities.CriticalAccessEntry;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BlockChart {

    Font myFont = new Font("Calibri (Textkörper)", 0, 11);

    public void writeChartToDocumentAccesPattern(XWPFDocument document, List<CriticalAccessEntry> entries) throws Exception {

        // get image as stream
        // BufferedImage chart = createPieChart(250, 250, 10, 20, 30, 40);

        int width = 450;
        int height = 320;
        BufferedImage blockchart = blockchart(entries, width, height, "AccessPattern");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(blockchart, "png", os);
        InputStream chartInputStream = new ByteArrayInputStream(os.toByteArray());

        // write image to document
        XWPFParagraph paragraph;
        XWPFRun run;
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.addPicture(chartInputStream, document.PICTURE_TYPE_PNG, null, Units.toEMU(width), Units.toEMU(height));
    }

    public void writeChartToDocumentUser(XWPFDocument document, List<CriticalAccessEntry> entries) throws Exception {

        // get image as stream
        // BufferedImage chart = createPieChart(250, 250, 10, 20, 30, 40);

        int width = 450;
        int height = 320;
        BufferedImage blockchart = blockchart(entries, width, height, "User");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(blockchart, "png", os);
        InputStream chartInputStream = new ByteArrayInputStream(os.toByteArray());

        // write image to document
        XWPFParagraph paragraph;
        XWPFRun run;
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.addPicture(chartInputStream, document.PICTURE_TYPE_PNG, null, Units.toEMU(width), Units.toEMU(height));
    }

    private BufferedImage blockchart(List<CriticalAccessEntry> entries, int weight, int height, String type) throws Exception {

        // System.out.println(entries);


        BufferedImage bufferedImage;

        if (type == "AccessPattern") {

            ArrayList<String> AccessPatternID = new ArrayList<String>();
            ArrayList<Integer> NumberUsers = new ArrayList<>();

            for (CriticalAccessEntry entry : entries) {

                int count = 0;
                int found = 0;

                while (AccessPatternID.size() > count) {

                    if (AccessPatternID.get(count) == entry.getAccessPattern().getUsecaseId()) {
                        NumberUsers.set(count, NumberUsers.get(count) + 1);
                        found = 1;
                    }
                    count++;
                }
                if (found == 0) {
                    AccessPatternID.add(entry.getAccessPattern().getUsecaseId());
                    NumberUsers.add(1);
                }
            }
            bufferedImage = createBlockChart(AccessPatternID, NumberUsers, "pattern_x", weight, height, type);
        } else {
            ArrayList<String> UserID = new ArrayList<String>();
            ArrayList<Integer> NumberAccessPattern = new ArrayList<>();

            for (CriticalAccessEntry entry : entries) {

                int count = 0;
                int found = 0;

                while (UserID.size() > count) {

                    if (UserID.get(count) == entry.getUsername()) {
                        NumberAccessPattern.set(count, NumberAccessPattern.get(count) + 1);
                        found = 1;
                    }
                    count++;
                }
                if (found == 0) {
                    UserID.add(entry.getUsername());
                    NumberAccessPattern.add(1);
                }
            }
            bufferedImage = createBlockChart(UserID, NumberAccessPattern, "pattern_x", weight, height, type);
        }

        //bufferedImage = createBlockChart02(UserID, NumberAccessPattern, "pattern_x", weight, height);

        return bufferedImage;
    }

    private BufferedImage createBlockChart(ArrayList<String> ID, ArrayList<Integer> Number, String type, int width, int height, String typeText) throws Exception {


        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.lightGray);

        int y = 270;

        // draw lines

        for (int i = 1; i < 10; i++) {

            g2d.drawLine(40, y, 440, y);

            //System.out.println(y);

            y = y - 27;

        }

        // Determine the maximal number of user

        int maxuser = 0;

        for (int i = 0; i < Number.size(); i++) {
            if (Number.get(i) > maxuser) {

                maxuser = Number.get(i);
            }
        }

        // System.out.println("maxuser :");
        // System.out.println(maxuser);

        // Calculating y range per user

        int yrange = 216 / maxuser;

        // draw charts + x text

        int x = 400 / ID.size() / 2 + 40;

        for (int i = 0; i < ID.size(); i++) {

            y = 54 + ((maxuser - Number.get(i)) * yrange);

            g2d.setColor(Color.red);
            g2d.fillRect(x, y, 10, (Number.get(i) * yrange));
            g2d.setColor(Color.darkGray);
            g2d.setFont(myFont);
            g2d.drawString(ID.get(i), x, 290);
            x = x + 400 / ID.size();
        }

        // Draw text for axis

        if (typeText == "AccessPattern") {
            g2d.drawString("Access", 390, 310);
            g2d.drawString("Pattern IDs", 390, 320);
            g2d.drawString("Numbers ", 0, 20);
            g2d.drawString("of violated ", 0, 30);
            g2d.drawString("users", 0, 40);
        } else {
            g2d.drawString("User IDs", 403, 310);
            g2d.drawString("Numbers ", 0, 20);
            g2d.drawString("of violated ", 0, 30);
            g2d.drawString("Access Patterns", 0, 40);

        }

        //----------------------------------------------------------------------

        //System.out.println("MaxUser "+maxuser);

        double dmaxuser = maxuser;

        y = 270;
        x = 10;
        // int anzahlY = maxuser % 5;

        int yadd = 216 / 4;

        //System.out.println("yadd "+yadd);

        g2d.drawString(" " + 0, x, y);

        y = y - yadd;

        double result = (dmaxuser / 4);
        double result1 = result;

        for (int i = 0; i < 5; i++) {

            System.out.println("y "+y);
            System.out.println("result "+result);
            g2d.drawString(" " + result, x, y);
            y = y - yadd;
            result = result + result1;

        }


        // int numberytext =

        //   for (int i = 1 ; i<= bbsbd; i++){

        //  }
        // draw y text


        return bufferedImage;
    }

}
