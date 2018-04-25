
package excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

//@SuppressWarnings("all") // remove annotation lateron
public class WhitelistImportHelper {

    public List<WhitelistUser> importWhitelist(String filePath) throws Exception {

        // open excel file
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        // init empty list and row index counter
        List<WhitelistUser> list = new ArrayList<WhitelistUser>();
        int rowIndex = 0;

        // go through excel rows
        for (Row row : sheet) {

            // don't read in header row
            if (rowIndex++ == 0) { continue; }

            // get value from columns A and B as string
            String userID = row.getCell(0).getStringCellValue();
            String userName = row.getCell(1).getStringCellValue();

            // create new user and add it to list
            list.add(new WhitelistUser(userID, userName));
        }

        // dispose file handle
        workbook.close();
        excelFile.close();

        return list;
    }

}
