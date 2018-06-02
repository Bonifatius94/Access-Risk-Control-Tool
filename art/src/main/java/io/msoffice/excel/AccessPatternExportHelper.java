package io.msoffice.excel;

import data.entities.AccessCondition;
import data.entities.AccessConditionType;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.ConditionLinkage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * This class helps exporting config data to a MS Excel file.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class AccessPatternExportHelper {

    /**
     * This method exports config data to a MS Excel file (file is always overwritten, data is in first data sheet).
     *
     * @param filePath the file path of the MS Excel output file
     * @param patterns the config that is exported
     * @throws Exception caused by a file stream error or a data error due to invalid config data
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void exportAuthorizationPattern(String filePath, List<AccessPattern> patterns) throws Exception {

        // create new workbook and worksheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // write each pattern to worksheet
        patterns.forEach(x -> writeAuthPattern(sheet, x));

        // amplify column widths
        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn(i);
        }

        // write workbook to file
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            workbook.write(fos);
        }
    }

    /**
     * This method writes the overloaded auth pattern to the overloaded MS Excel data sheet.
     *
     * @param sheet   the MS Excel data sheet the auth pattern is applied to
     * @param pattern the auth pattern that is written to the specified MS Excel data sheet
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void writeAuthPattern(Sheet sheet, AccessPattern pattern) {

        int startIndex = sheet.getPhysicalNumberOfRows();

        // write usecase id and description
        Row row = sheet.createRow(startIndex);
        row.createCell(0).setCellValue(pattern.getUsecaseId());
        row.createCell(3).setCellValue(pattern.getDescription());

        if (pattern.getLinkage() == ConditionLinkage.None && pattern.getConditions().size() == 1) {

            // write simple condition
            AccessCondition condition = new ArrayList<>(pattern.getConditions()).get(0);
            writeCondition(sheet, condition, false);

        } else if (pattern.getConditions().size() > 1) {

            // write condition linkage
            row = sheet.createRow(startIndex + 1);
            row.createCell(1).setCellValue(pattern.getLinkage().toString());

            // write complex conditions
            for (AccessCondition condition : pattern.getConditions()) {
                writeCondition(sheet, condition, true);
            }

        } else {

            // invalid auth pattern => throw exception (this section should never be hit by program)
            throw new IllegalArgumentException("Authorization pattern is of an unknown type and can therefore not be exported to excel file.");
        }
    }

    /**
     * This method writes the overloaded condition to the overloaded MS Excel file (either in complex or simple mode).
     * It therefore determines whether the overloaded condition is of auth profile or auth pattern type and calls
     * a sub method accordingly.
     *
     * @param sheet     the MS Excel data sheet the condition is applied to
     * @param condition the condition that
     * @param isComplex a boolean value that indicates whether the specified condition is part of a simple or complex auth pattern
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void writeCondition(Sheet sheet, AccessCondition condition, boolean isComplex) {

        if (condition.getType() == AccessConditionType.ProfileCondition) {

            // write auth profile condition
            writeAuthProfileCondition(sheet, condition.getProfileCondition(), isComplex);

        } else if (condition.getType() == AccessConditionType.PatternCondition) {

            // write auth profile condition
            writeAuthPatternCondition(sheet, condition.getPatternCondition(), isComplex);

        } else {

            // there is no other type => throw exception (this section should never be hit by program)
            throw new IllegalArgumentException("Condition is of an unknown type and can therefore not be exported to excel file.");
        }
    }

    /**
     * This method writes the overloaded auth profile condition to the overloaded MS Excel data sheet (either in complex or simple mode).
     *
     * @param sheet     the MS Excel data sheet the auth profile condition is applied to
     * @param condition the auth profile condition that is written to the specified MS Excel data sheet
     * @param isComplex a boolean value that indicates whether the specified condition is part of a simple or complex auth pattern
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void writeAuthProfileCondition(Sheet sheet, AccessProfileCondition condition, boolean isComplex) {

        // create new row
        int startIndex = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow(startIndex);

        // write cond markup
        row.createCell(isComplex ? 2 : 1).setCellValue("COND");

        // write user profile
        row.createCell(isComplex ? 9 : 8).setCellValue(condition.getProfile());
    }

    /**
     * This method writes the overloaded auth pattern condition to the overloaded MS Excel data sheet (either in complex or simple mode).
     *
     * @param sheet     the MS Excel data sheet the auth profile condition is applied to
     * @param condition the auth pattern condition that is written to the specified MS Excel data sheet
     * @param isComplex a boolean value that indicates whether the specified condition is part of a simple or complex auth pattern
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void writeAuthPatternCondition(Sheet sheet, AccessPatternCondition condition, boolean isComplex) {

        int startRowIndex = sheet.getPhysicalNumberOfRows();
        int rowIndex = startRowIndex;

        for (AccessPatternConditionProperty property : condition.getProperties()) {

            // create new row
            int columnIndex = isComplex ? 3 : 2;
            Row row = sheet.createRow(rowIndex);

            // write data
            row.createCell(columnIndex++).setCellValue(property.getAuthObject());
            row.createCell(columnIndex++).setCellValue(property.getAuthObjectProperty());
            row.createCell(columnIndex++).setCellValue(property.getValue1());
            row.createCell(columnIndex++).setCellValue(property.getValue2());
            row.createCell(columnIndex++).setCellValue(property.getValue3());
            row.createCell(columnIndex).setCellValue(property.getValue4());

            // increment row index
            rowIndex++;
        }

        // write cond markup
        sheet.getRow(startRowIndex).createCell(isComplex ? 2 : 1).setCellValue("COND");
    }

}
