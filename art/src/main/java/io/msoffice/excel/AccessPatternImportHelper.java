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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * This class helps to import config data from MS Excel files.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class AccessPatternImportHelper {

    // TODO: throw an exception if a profile condition is part of a complex pattern

    /**
     * This method imports config data from a MS Excel file (data is only taken from first datasheet).
     *
     * @param filePath the file path of the MS Excel file that contains the config data
     * @return a list of auth patterns (config)
     * @throws Exception caused by file stream error or parsing error due to invalid data formats
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public List<AccessPattern> importAuthorizationPattern(String filePath) throws Exception {

        // open excel file
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        // init empty list and row index counter
        List<AccessPattern> list = new ArrayList<>();
        List<Row> tempRows = new ArrayList<>();
        boolean isFirstRow = true;

        // go through excel rows
        for (Row row : sheet) {

            // parse pattern name
            Cell patternNameCell = row.getCell(0);

            // check if new pattern begins
            if (!isFirstRow && patternNameCell != null) {
                String patternName = patternNameCell.getStringCellValue();

                if (patternName != null && !patternName.isEmpty()) {
                    // parse pattern from rows before and add pattern to list
                    list.add(getAuthPattern(tempRows));
                    tempRows = new ArrayList<>();
                }
            }

            tempRows.add(row);
            isFirstRow = false;
        }

        // parse remaining rows
        if (tempRows.size() >= 2) {
            list.add(getAuthPattern(tempRows));
        }

        // dispose file handle
        workbook.close();
        excelFile.close();

        return list;
    }

    /**
     * This method parses the auth pattern from the overloaded rows.
     * It therefore finds out whether the auth pattern is of simple or complex type
     * and then calls a method to parse the specific pattern data. It also parses
     * the pattern name and description.
     *
     * @param rows the rows containing auth pattern data
     * @return the auth pattern parsed from the overloaded rows
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessPattern getAuthPattern(List<Row> rows) {

        AccessPattern pattern;
        String patternName = rows.get(0).getCell(0).getStringCellValue();
        String patternDescription = rows.get(0).getCell(3).getStringCellValue();
        String patternTypeIndicator = rows.get(1).getCell(1).getStringCellValue();

        if (patternTypeIndicator == null || patternTypeIndicator.isEmpty()) {
            throw new IllegalArgumentException("Pattern type not set. Needs to be COND, AND or OR.");
        } else if (patternTypeIndicator.toUpperCase().equals("COND")) {
            pattern = getSimpleAuthPattern(rows);
        } else /*if (patternTypeIndicator.toUpperCase().equals("OR") || patternTypeIndicator.toUpperCase().equals("AND"))*/ {
            pattern = getComplexAuthPattern(rows);
        }

        // set name and description
        pattern.setUsecaseId(patternName);
        pattern.setDescription(patternDescription);

        return pattern;
    }

    /**
     * This method parses a simple auth pattern from the rows overloaded. It therefore parses
     * the only condition and applies it to a new instance of an auth pattern.
     *
     * @param rows the rows containing auth pattern data
     * @return an auth pattern of simple type
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessPattern getSimpleAuthPattern(List<Row> rows) {

        AccessCondition condition = getCondition(rows.subList(1, rows.size()), false);
        return new AccessPattern(condition);
    }

    /**
     * This method parses a complex auth pattern from the rows overloaded. It therefore parses
     * the condition linkage and all condition.
     *
     * @param rows the rows containing auth pattern data
     * @return an auth pattern of complex type
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessPattern getComplexAuthPattern(List<Row> rows) {

        List<Row> tempRows = new ArrayList<>();
        List<AccessCondition> conditions = new ArrayList<>();

        String linkageAsString = rows.get(1).getCell(1).getStringCellValue();
        ConditionLinkage linkage = linkageAsString.toUpperCase().equals("OR") ? ConditionLinkage.Or : ConditionLinkage.And;

        int counter = 0;
        boolean isFirstRow = true;

        // go through conditions
        for (Row row : rows.subList(2, rows.size())) {

            if (counter > 10) {
                throw new IllegalArgumentException("Too many conditions detected in complex pattern");
            }

            // parse conditions
            String condIndicator = row.getCell(2).getStringCellValue();

            if (!isFirstRow && (condIndicator != null && condIndicator.toUpperCase().equals("COND"))) {

                // parse pattern from rows before and add pattern to list
                conditions.add(getCondition(tempRows, true));
                tempRows = new ArrayList<>();
                counter++;
            }

            tempRows.add(row);
            isFirstRow = false;
        }

        // parse remaining rows
        if (tempRows.size() > 0) {
            conditions.add(getCondition(tempRows, true));
        }

        return new AccessPattern(conditions, linkage);
    }

    /**
     * This method parses a condition from the rows overloaded. It therefore determines the condition type
     * and then parses the condition data accordingly.
     *
     * @param rows      the rows containing condition data
     * @param isComplex a boolean value that indicates where the COND markup starts
     * @return a condition that is either an instance of an auth profile condition or an auth pattern condition
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private AccessCondition getCondition(List<Row> rows, boolean isComplex) {

        AccessCondition condition = new AccessCondition();
        Cell temp = rows.get(0).getCell(isComplex ? 9 : 8);

        if (temp != null && temp.getStringCellValue() != null && !temp.getStringCellValue().isEmpty()) {

            // case: profile condition
            String profile = temp.getStringCellValue();
            condition.setProfileCondition(new AccessProfileCondition(profile));

        } else {

            // case: auth pattern condition
            int startIndex = isComplex ? 3 : 2;
            List<AccessPatternConditionProperty> properties = new ArrayList<>();

            for (Row row : rows) {
                // parse auth object and property name
                int index = startIndex;
                final String authorizationObject = row.getCell(index).getStringCellValue();
                final String propertyName = row.getCell(++index).getStringCellValue();

                // parse values
                temp = row.getCell(++index);
                final String value1 = (temp != null) ? temp.getStringCellValue() : null;
                temp = row.getCell(++index);
                final String value2 = (temp != null) ? temp.getStringCellValue() : null;
                temp = row.getCell(++index);
                final String value3 = (temp != null) ? temp.getStringCellValue() : null;
                temp = row.getCell(++index);
                final String value4 = (temp != null) ? temp.getStringCellValue() : null;

                properties.add(new AccessPatternConditionProperty(authorizationObject, propertyName, value1, value2, value3, value4));
            }

            condition.setPatternCondition(new AccessPatternCondition(properties));
        }

        return condition;
    }

}
