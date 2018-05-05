package excel.whitelist;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class WhitelistExportHelper {

    public void exportWhitelist(String filePath, List<WhitelistUser> whitelistUsers) throws Exception {

        // create new workbook and worksheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int index = 0;

        // write users to worksheet
        for (WhitelistUser user : whitelistUsers) {

            // create new row
            Row row = sheet.createRow(index);

            // write data to row
            row.createCell(0).setCellValue(user.getUsecaseID());
            row.createCell(1).setCellValue(user.getUsername());

            index++;
        }

        // write workbook to file
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            workbook.write(fos);
        }
    }

}
