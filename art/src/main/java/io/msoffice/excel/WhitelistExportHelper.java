package io.msoffice.excel;

import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * This class helps to export a whitelist to a MS Excel file.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class WhitelistExportHelper {

    /**
     * This method exports a whitelist to a MS Excel file (file is always overwritten, data is in first data sheet).
     *
     * @param filePath  file path of the MS Excel output file
     * @param whitelist whitelist of users to be exported to MS Excel file
     * @throws Exception caused by file stream errors or errors during MS Excel export
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void exportWhitelist(String filePath, Whitelist whitelist) throws Exception {

        // create new workbook and worksheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // add header line
        Row firstRow = sheet.createRow(0);
        firstRow.createCell(0).setCellValue("Usecase ID");
        firstRow.createCell(1).setCellValue("Username");

        int rowIndex = 1;

        // write users to worksheet
        for (WhitelistEntry user : whitelist.getEntries()) {

            // create new row
            Row row = sheet.createRow(rowIndex);

            // write data to row
            row.createCell(0).setCellValue(user.getUsecaseId());
            row.createCell(1).setCellValue(user.getUsername());

            rowIndex++;
        }

        // write workbook to file
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            workbook.write(fos);
        }
    }

}
