package data.localdb;

import data.entities.analysis.AccessConditionHistory;
import data.entities.analysis.AccessPatternConditionHistory;
import data.entities.analysis.AccessPatternConditionPropertyHistory;
import data.entities.analysis.AccessPatternHistory;
import data.entities.analysis.AccessPatternXAccessConditionMapHistory;
import data.entities.analysis.AccessProfileConditionHistory;
import data.entities.analysis.ConfigurationHistory;
import data.entities.analysis.ConfigurationXAccessPatternMapHistory;
import data.entities.analysis.CriticalAccessEntry;
import data.entities.analysis.CriticalAccessList;
import data.entities.analysis.WhitelistEntryHistory;
import data.entities.analysis.WhitelistHistory;

import data.entities.config.AccessCondition;
import data.entities.config.AccessPattern;
import data.entities.config.AccessPatternCondition;
import data.entities.config.AccessPatternConditionProperty;
import data.entities.config.AccessPatternXAccessConditionMap;
import data.entities.config.AccessProfileCondition;
import data.entities.config.Configuration;
import data.entities.config.ConfigurationXAccessPatternMap;
import data.entities.config.Whitelist;
import data.entities.config.WhitelistEntry;

import java.util.ArrayList;
import java.util.List;

public class ArtDbContext extends H2ContextBase {

    public ArtDbContext(String username, String password) {
        super(username, password);
    }

    @Override
    protected List<Class> getAnnotatedClasses() {

        List<Class> list = new ArrayList<>();

        // config schema
        list.add(Configuration.class);
        list.add(ConfigurationXAccessPatternMap.class);
        list.add(AccessPattern.class);
        list.add(AccessPatternXAccessConditionMap.class);
        list.add(AccessCondition.class);
        list.add(AccessProfileCondition.class);
        list.add(AccessPatternCondition.class);
        list.add(AccessPatternConditionProperty.class);
        list.add(Whitelist.class);
        list.add(WhitelistEntry.class);

        // analysis schema
        list.add(CriticalAccessList.class);
        list.add(CriticalAccessEntry.class);
        list.add(ConfigurationHistory.class);
        list.add(ConfigurationXAccessPatternMapHistory.class);
        list.add(AccessPatternHistory.class);
        list.add(AccessPatternXAccessConditionMapHistory.class);
        list.add(AccessConditionHistory.class);
        list.add(AccessProfileConditionHistory.class);
        list.add(AccessPatternConditionHistory.class);
        list.add(AccessPatternConditionPropertyHistory.class);
        list.add(WhitelistHistory.class);
        list.add(WhitelistEntryHistory.class);

        return list;
    }

}
