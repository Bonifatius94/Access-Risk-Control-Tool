package excel.config;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationPatternImportHelper {

    public List<AuthorizationPattern> importAccessPattern(String filePath) throws Exception {

        // open excel file
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        // init empty list and row index counter
        List<AuthorizationPattern> list = new ArrayList<>();
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
                    list.add(getAuthorizationPattern(tempRows));
                    tempRows = new ArrayList<>();
                }
            }

            tempRows.add(row);
            isFirstRow = false;
        }

        // parse remaining rows
        if (tempRows.size() >= 2) { list.add(getAuthorizationPattern(tempRows)); }

        // dispose file handle
        workbook.close();
        excelFile.close();

        return list;
    }

    private AuthorizationPattern getAuthorizationPattern(List<Row> rows) {

        AuthorizationPattern pattern;
        String patternName = rows.get(0).getCell(0).getStringCellValue();
        String patternDescription = rows.get(0).getCell(3).getStringCellValue();
        String patternTypeIndicator = rows.get(1).getCell(1).getStringCellValue();

        if (patternTypeIndicator == null || patternTypeIndicator.isEmpty()) {
            throw new IllegalArgumentException("Pattern type not set. Needs to be COND, AND or OR.");
        }
        else if (patternTypeIndicator.toUpperCase().equals("COND")) {
            pattern = getSimpleAuthorizationPattern(rows);
        }
        else /*if (patternTypeIndicator.toUpperCase().equals("OR") || patternTypeIndicator.toUpperCase().equals("AND"))*/ {
            pattern = getComplexAuthorizationPattern(rows);
        }

        // set name and description
        pattern.setUsecaseID(patternName);
        pattern.setDescription(patternDescription);

        return pattern;
    }

    private AuthorizationPattern getSimpleAuthorizationPattern(List<Row> rows) {

        ICondition condition = getCondition(rows.subList(1, rows.size()), false);
        return new AuthorizationPattern(condition);
    }

    private AuthorizationPattern getComplexAuthorizationPattern(List<Row> rows) {

        List<Row> tempRows = new ArrayList<>();
        List<ICondition> conditions = new ArrayList<>();

        String linkageAsString = rows.get(1).getCell(1).getStringCellValue();
        ConditionLinkage linkage = linkageAsString.toUpperCase().equals("OR") ? ConditionLinkage.Or : ConditionLinkage.And;

        int counter = 0;
        boolean isFirstRow = true;

        // go through conditions
        for (Row row : rows.subList(2, rows.size())) {

            if (counter > 10) { throw new IllegalArgumentException("Too many conditions detected in complex pattern"); }

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
        if (tempRows.size() > 0) { conditions.add(getCondition(tempRows, true)); }

        return new AuthorizationPattern(conditions, linkage);
    }

    private ICondition getCondition(List<Row> rows, boolean isComplex) {

        ICondition condition;
        Cell temp = rows.get(0).getCell(isComplex ? 9 : 8);

        if (temp != null && temp.getStringCellValue() != null && !temp.getStringCellValue().isEmpty()) {
            // case: profile condition
            String profile = temp.getStringCellValue();
            condition = new AuthorizationProfileCondition(profile);
        }
        else {
            // case: auth pattern condition
            int startIndex = isComplex ? 3 : 2;
            List<AuthorizationPatternConditionProperty> properties = new ArrayList<>();

            for (Row row : rows) {

                // parse auth object and property name
                int index = startIndex;
                String authorizationObject = row.getCell(index).getStringCellValue();
                String propertyName = row.getCell(++index).getStringCellValue();

                // parse values
                temp = row.getCell(++index);
                String value1 = (temp != null) ? temp.getStringCellValue() : null;
                temp = row.getCell(++index);
                String value2 = (temp != null) ? temp.getStringCellValue() : null;
                temp = row.getCell(++index);
                String value3 = (temp != null) ? temp.getStringCellValue() : null;
                temp = row.getCell(++index);
                String value4 = (temp != null) ? temp.getStringCellValue() : null;

                properties.add(new AuthorizationPatternConditionProperty(authorizationObject, propertyName, value1, value2, value3, value4));
            }

            condition = new AuthorizationPatternCondition(properties);
        }

        return condition;
    }

}
