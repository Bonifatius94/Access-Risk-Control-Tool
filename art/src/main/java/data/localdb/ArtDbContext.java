package data.localdb;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.Configuration;
import data.entities.ConfigurationXAccessPatternMap;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.util.ArrayList;
import java.util.List;

public class ArtDbContext extends H2ContextBase {

    public ArtDbContext(String username, String password) {
        super(username, password);
    }

    @Override
    protected List<Class> getAnnotatedClasses() {

        List<Class> list = new ArrayList<>();

        // configuration
        list.add(Configuration.class);
        list.add(ConfigurationXAccessPatternMap.class);

        // access pattern
        list.add(AccessPattern.class);
        list.add(AccessCondition.class);
        list.add(AccessProfileCondition.class);
        list.add(AccessPatternCondition.class);
        list.add(AccessPatternConditionProperty.class);

        // whitelist
        list.add(Whitelist.class);
        list.add(WhitelistEntry.class);

        // sap query
        list.add(CriticalAccessQuery.class);
        list.add(CriticalAccessEntry.class);

        // sap config
        list.add(SapConfiguration.class);

        // TODO: only add this entity during query, not here (see official hibernate guide chapter 16)
        // database user accounts
        //list.add(DbUser.class);

        return list;
    }

}
