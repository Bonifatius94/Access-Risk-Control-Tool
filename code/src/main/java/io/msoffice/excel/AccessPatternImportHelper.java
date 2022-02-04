package io.msoffice.excel;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.ConditionLinkage;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * This class helps to import config data from MS Excel files.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class AccessPatternImportHelper {
    
    /**
     * This method imports config data from a MS Excel file (data is only taken from first datasheet).
     *
     * @param filePath the file path of the MS Excel file that contains the config data
     * @return a list of auth patterns (config)
     * @throws Exception caused by file stream error or parsing error due to invalid data formats
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public List<AccessPattern> importAccessPatterns(String filePath) throws Exception {

        // open excel file
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
        XSSFSheet sheet = workbook.getSheetAt(0);

        // init empty list and row index counter
        List<List<XSSFRow>> rowsPerPattern = getRowsPerPattern(sheet);
        List<AccessPattern> patterns = rowsPerPattern.parallelStream().map(rows -> getAccessPattern(rows)).collect(Collectors.toList());

        // dispose file handle
        workbook.close();
        excelFile.close();

        return patterns;
    }

    private List<List<XSSFRow>> getRowsPerPattern(XSSFSheet sheet) {

        List<List<XSSFRow>> rowsPerPattern = new ArrayList<>();
        List<XSSFRow> tempRows = new ArrayList<>();
        boolean isFirstRow = true;

        // go through excel rows
        for (Row abstractRow : sheet) {

            if (abstractRow instanceof XSSFRow) {

                XSSFRow row = (XSSFRow) abstractRow;

                // parse pattern name
                Cell patternNameCell = row.getCell(0);

                // check if new pattern begins
                if (!isFirstRow && patternNameCell != null && !patternNameCell.getStringCellValue().trim().isEmpty()) {

                    String patternName = patternNameCell.getStringCellValue();

                    if (patternName != null && !patternName.trim().isEmpty()) {

                        // parse pattern from rows before and add pattern to list
                        rowsPerPattern.add(tempRows);
                        tempRows = new ArrayList<>();
                    }
                }

                tempRows.add(row);
                isFirstRow = false;
            }
        }

        // parse remaining rows
        if (tempRows.size() >= 2) {
            rowsPerPattern.add(tempRows);
        }

        return rowsPerPattern;
    }

    /**
     * This method parses the access pattern from the overloaded rows.
     * It therefore finds out whether the access pattern contains exactly one or more conditions
     * and then calls a method to parse the specific pattern data. It also parses the pattern name and description.
     *
     * @param rows the rows containing access pattern data
     * @return the access pattern parsed from the overloaded rows
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessPattern getAccessPattern(List<XSSFRow> rows) {

        AccessPattern pattern;
        String patternName = rows.get(0).getCell(0).getStringCellValue().trim();
        String patternDescription = rows.get(0).getCell(3).getStringCellValue().trim();
        String patternTypeIndicator = rows.get(1).getCell(1).getStringCellValue();

        if (patternTypeIndicator == null || patternTypeIndicator.trim().isEmpty()) {

            throw new IllegalArgumentException("the pattern type is not set (e.g. COND, AND or OR)");

        } else if (patternTypeIndicator.trim().toUpperCase().equals("COND")) {

            pattern = getSingleConditionAccessPattern(rows);

        } else /*if (patternTypeIndicator.toUpperCase().equals("OR") || patternTypeIndicator.toUpperCase().equals("AND"))*/ {

            pattern = getMultiConditionAccessPattern(rows);
        }

        // set name and description
        pattern.setUsecaseId(patternName);
        pattern.setDescription(patternDescription);

        return pattern;
    }

    /**
     * This method parses a single-condition access pattern from the rows overloaded. It therefore parses
     * the only condition and applies it to a new instance of an auth pattern.
     *
     * @param rows the rows containing access pattern data
     * @return an access pattern of simple type
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessPattern getSingleConditionAccessPattern(List<XSSFRow> rows) {

        AccessCondition condition = getCondition(rows.subList(1, rows.size()), false);
        return new AccessPattern(condition);
    }

    /**
     * This method parses a multi-condition access pattern from the rows overloaded. It therefore parses
     * the condition linkage and all conditions.
     *
     * @param rows the rows containing access pattern data
     * @return an access pattern of complex type
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessPattern getMultiConditionAccessPattern(List<XSSFRow> rows) {

        String linkageAsString = rows.get(1).getCell(1).getStringCellValue();
        ConditionLinkage linkage = linkageAsString.trim().toUpperCase().equals("OR") ? ConditionLinkage.Or : ConditionLinkage.And;

        List<List<XSSFRow>> rowsPerCondition = getRowsPerCondition(rows);
        List<AccessCondition> conditions = rowsPerCondition.parallelStream().map(x -> getCondition(x, true)).collect(Collectors.toList());

        return new AccessPattern(conditions, linkage);
    }

    private List<List<XSSFRow>> getRowsPerCondition(List<XSSFRow> rows) {

        List<List<XSSFRow>> rowsPerCondition = new ArrayList<>();
        List<XSSFRow> tempRows = new ArrayList<>();

        int count = 0;
        boolean isFirstRow = true;

        // go through conditions
        for (XSSFRow row : rows.subList(2, rows.size())) {

            if (count > 10) {
                throw new IllegalArgumentException("too many conditions detected in multi-condition pattern");
            }

            // parse conditions
            Cell condIndicatorCell = row.getCell(2);

            if (condIndicatorCell != null) {

                String condIndicator = condIndicatorCell.getStringCellValue();

                if (!isFirstRow && (condIndicator != null && condIndicator.trim().toUpperCase().equals("COND"))) {

                    // get the rows that contain the condition
                    rowsPerCondition.add(tempRows);
                    tempRows = new ArrayList<>();
                    count++;
                }
            }

            tempRows.add(row);
            isFirstRow = false;
        }

        // parse remaining rows
        if (tempRows.size() > 0) {
            rowsPerCondition.add(tempRows);
        }

        return rowsPerCondition;
    }

    /**
     * This method parses a condition from the rows overloaded. It therefore determines the condition type
     * and then parses the condition data accordingly.
     *
     * @param rows      the rows containing condition data
     * @param isMultiCondition a boolean value that indicates where the COND markup starts
     * @return a condition that is either an instance of an auth profile condition or an auth pattern condition
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessCondition getCondition(List<XSSFRow> rows, boolean isMultiCondition) {

        AccessCondition condition = new AccessCondition();
        Cell profileCell = rows.get(0).getCell(isMultiCondition ? 9 : 8);

        if (profileCell != null && profileCell.getStringCellValue() != null && !profileCell.getStringCellValue().trim().isEmpty()) {

            // case: profile condition
            String profile = profileCell.getStringCellValue().trim();
            condition.setProfileCondition(new AccessProfileCondition(profile));

        } else {

            // case: access pattern condition
            int i = 0;
            int startIndex = isMultiCondition ? 3 : 2;
            List<AccessPatternConditionProperty> properties = new ArrayList<>();

            for (XSSFRow row : rows) {

                // parse auth object and property name
                int index = startIndex;
                final String authObject = row.getCell(index).getStringCellValue().trim();
                final String authObjectProperty = row.getCell(++index).getStringCellValue().trim();

                // parse values
                XSSFCell valueCell = row.getCell(++index);
                final String value1 = (valueCell != null) ? getValueFromCell(valueCell) : null;
                valueCell = row.getCell(++index);
                final String value2 = (valueCell != null) ? getValueFromCell(valueCell) : null;
                valueCell = row.getCell(++index);
                final String value3 = (valueCell != null) ? getValueFromCell(valueCell) : null;
                valueCell = row.getCell(++index);
                final String value4 = (valueCell != null) ? getValueFromCell(valueCell) : null;

                properties.add(new AccessPatternConditionProperty(authObject, authObjectProperty, value1, value2, value3, value4, i));
                i++;
            }

            condition.setPatternCondition(new AccessPatternCondition(properties));
        }

        return condition;
    }

    /**
     * This method helps parsing the content from an excel cell as a string.
     *
     * @param cell the cell to be parsed
     * @return the value of the cell as string
     */
    private String getValueFromCell(XSSFCell cell) {

        String value = null;

        if (cell != null) {

            // INFO: the deprecated functions had to be used because there was no function equally replacing the deprecated ones
            // TODO: check if there is a function replacing the deprecated ones
            CellType type = cell.getCellTypeEnum() == CellType.FORMULA ? cell.getCachedFormulaResultTypeEnum() : cell.getCellTypeEnum();

            switch (type) {
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                default:
                    // TODO: FORMULA type may not be supported. If the FORMULA type is required, apply changes here.
                    // get the raw value as string
                    value = cell.getRawValue();
                    break;
            }
        }

        if (value != null) {
            value = value.trim();
        }

        return value;
    }

}
