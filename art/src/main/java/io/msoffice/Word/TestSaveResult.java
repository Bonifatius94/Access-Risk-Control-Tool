package io.msoffice.Word;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.ConditionLinkage;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestSaveResult {

    public  TestSaveResult (int i) throws Exception {

        // get Access Pattern

        AccessPatternImportHelper helper = new AccessPatternImportHelper();

        List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

        //  System.out.println(patterns.toString());

        // Save results


        Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");

        //  System.out.println(whitelist);

        List<AccessCondition> conditions = new ArrayList<>(patterns.get(0).getConditions());

        // System.out.print(conditions);

        ConditionLinkage linkage = patterns.get(0).getLinkage();

        // System.out.print(linkage);

        AccessPattern pattern = new AccessPattern(conditions ,linkage);

        // System.out.print(pattern);

        CriticalAccessEntry entries =  new CriticalAccessEntry(pattern, "Test");

        Set<CriticalAccessEntry> entries1 = Collections.singleton(new CriticalAccessEntry(pattern, "Test"));

        // List<CriticalAccessEntry> entries2 = new CriticalAccessEntry(patterns,"test" );
        // System.out.print(entries1);

        entries.setId(1);
        entries.getAccessPattern().setId(1);

        // System.out.print( entries.getAccessPattern());

        CriticalAccessQuery query = new CriticalAccessQuery();

        query.setEntries(Collections.singletonList(entries));
        // query.setEntries(entries);

        query.setId(12);

        SapConfiguration sapConfig = new SapConfiguration();
        sapConfig.setSysNr("00");
        sapConfig.setServerDestination("Pertersserver");
        sapConfig.setClient("100");
        sapConfig.setLanguage("EN");
        sapConfig.setPoolCapacity("3");
        sapConfig.setCreatedBy("Hans");
        System.out.println(sapConfig);

        // System.out.print(query.getEntries().iterator().next().getAccessPattern());

        // query.setConfig(config);
        query.setSapConfig(sapConfig);
        query.getSapConfig();
        // System.out.println(query.getSapConfig());

        //System.out.print(entries.getAccessPattern());
        //System.out.print(entries.getUsername());
        //System.out.print(entries.getId());
        //System.out.print(entries.getAccessPattern().getId());


        SaveResult SaveResultNew = new SaveResult(query, whitelist);

    }
}
