package io.msoffice.excel.ResultWordExport;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.ConditionLinkage;
import data.entities.CriticalAccessEntry;
import data.entities.Whitelist;
import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;
import java.util.ArrayList;

import java.util.List;

public class TestSaveResult {

    public  TestSaveResult (int i) throws Exception {

        // get Access Pattern

        AccessPatternImportHelper helper = new AccessPatternImportHelper();

        List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

        //  System.out.println(patterns.toString());

        // Save results


        Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

        //  System.out.println(whitelist);

        List<AccessCondition> conditions = new ArrayList<>(patterns.get(1).getConditions());

        // System.out.print(uwe);

        ConditionLinkage linkage = patterns.get(0).getLinkage();

        // System.out.print(linkage);

        AccessPattern pattern = new AccessPattern(conditions ,linkage);

        CriticalAccessEntry entries =  new CriticalAccessEntry(pattern, "Test") ;

        entries.setId(1);
        entries.getAccessPattern().setId(1);


        //System.out.print(entries.getAccessPattern());
        //System.out.print(entries.getUsername());
        //System.out.print(entries.getId());
        //System.out.print(entries.getAccessPattern().getId());

        // SaveResult SaveResult = new SaveResult(patterns, whitelist);

        SaveResult SaveResultNew = new SaveResult(entries, whitelist);

    }
}
