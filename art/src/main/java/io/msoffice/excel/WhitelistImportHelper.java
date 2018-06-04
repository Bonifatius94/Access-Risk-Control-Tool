package io.msoffice.excel;

import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * This class helps importing whitelist data from a MS Excel file.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class WhitelistImportHelper {

    /**
     * This method imports a whitelist from a MS Excel file (data is only taken from the first data sheet).
     *
     * @param filePath file path of the MS Excel input file
     * @throws Exception caused by file stream errors or errors during MS Excel export
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public Whitelist importWhitelist(String filePath) throws Exception {

        // open excel file
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        // init empty list and row index counter
        List<WhitelistEntry> list = new ArrayList<WhitelistEntry>();
        int rowIndex = 0;

        // go through excel rows
        for (Row row : sheet) {

            // don't read in header row
            if (rowIndex++ == 0) {
                continue;
            }

            // get value from columns A and B as string
            String userId = row.getCell(0).getStringCellValue();
            String userName = row.getCell(1).getStringCellValue();

            // create new user and add it to list
            list.add(new WhitelistEntry(userId, userName));
        }

        // dispose file handle
        workbook.close();
        excelFile.close();

        Whitelist result = new Whitelist();
        result.setEntries(list);

        return result;
    }

}
