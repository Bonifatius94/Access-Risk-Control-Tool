package ResultExport;

import data.entities.AccessCondition;
import data.entities.AccessPattern;

import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;

import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class ExportWord extends SaveResult {

    public XWPFDocument document;
    public XWPFParagraph p1;
    public XWPFParagraph p2;
    public XWPFRun run;
    public XWPFRun runTwo;

    /**
     * Creates a new ButtonCell with the given icon and the function to call.
     *
     * @param filePath the name of the document
     * @param patterns the list that should be printed
     */

    public ExportWord(String filePath, List<AccessPattern> patterns, Whitelist whitelist) throws Exception {

        //create blank Document
        document = new XWPFDocument();
        p1 = document.createParagraph();
        run = p1.createRun();

        for (int i = 0; i < 1; i++) {
            run.setText("Usecase ID: ");
            run.addBreak();
            run.setText(patterns.get(i).getUsecaseId());
            run.addBreak();
            run.setText("Description: ");
            run.addBreak();
            run.setText(patterns.get(i).getDescription());
            run.addBreak();

            //Create table
            XWPFTable table = document.createTable();

            //create first row
            XWPFTableRow tableRow = table.getRow(0);
            tableRow.getCell(0).setText("                  ");
            tableRow.addNewTableCell().setText("                 ");
            tableRow.addNewTableCell().setText("Authorization Object");
            tableRow.addNewTableCell().setText("Value 1");
            tableRow.addNewTableCell().setText("Value 2");
            tableRow.addNewTableCell().setText("Value 3");
            tableRow.addNewTableCell().setText("Value 4");

            //column width in Twentieths of a Point
            XWPFTableCell cell = tableRow.getCell(0);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1000));
            cell = tableRow.getCell(1);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1000));
            cell = tableRow.getCell(2);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400));
            cell = tableRow.getCell(3);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400));
            cell = tableRow.getCell(4);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400));
            cell = tableRow.getCell(5);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400));
            cell = tableRow.getCell(6);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1340));

            run.setText(patterns.get(i).getLinkage().toString());

            int j = 0;

            for (int h = 0; h < patterns.get(0).getConditions().size(); h++) {

                AccessCondition condition = new ArrayList<>(patterns.get(0).getConditions()).get(h);
                AccessPatternCondition PatternCondition = condition.getPatternCondition();

                for (AccessPatternConditionProperty property : PatternCondition.getProperties()) {
                    tableRow = table.createRow();
                    tableRow.getCell(0).setText(" ");
                    tableRow.getCell(1).setText(" ");
                    tableRow.getCell(2).setText(property.getAuthObject());
                    tableRow.getCell(3).setText(property.getValue1());
                    tableRow.getCell(4).setText(property.getValue2());
                    tableRow.getCell(5).setText(property.getValue3());
                    tableRow.getCell(6).setText(property.getValue4());
                }
            }
        }

        XWPFParagraph para2 = document.createParagraph();
        XWPFRun run2 = para2.createRun();
        run2.addBreak();
        run2.setText("Whitelist: ");
        //Print whitelist
        for (WhitelistEntry user : whitelist.getEntries()) {
            // write data to row
            run2.addBreak();
            run2.setText(user.getUsecaseId());
            run2.addTab();
            run2.setText(user.getUsername());
            run2.addTab();
        }
        //Write the Document in file system
        FileOutputStream out = new FileOutputStream(new File(filePath));
        document.write(out);
        out.close();
        System.out.println("createdocument.docx written successully");
    }
}