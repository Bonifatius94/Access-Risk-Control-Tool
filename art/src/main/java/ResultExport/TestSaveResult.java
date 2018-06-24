package ResultExport;

import data.entities.AccessPattern;
import data.entities.Whitelist;
import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;

import java.util.List;

public class TestSaveResult {

    public  TestSaveResult (int i) throws Exception {

        // get Access Pattern

        AccessPatternImportHelper helper = new AccessPatternImportHelper();

        List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

        // System.out.println(patterns.toString());

        // Save results

        // Paramter List<AccessPattern> patterns

        Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

        System.out.println(whitelist);

        SaveResult SaveResult = new SaveResult(patterns, whitelist);

    }
}
  