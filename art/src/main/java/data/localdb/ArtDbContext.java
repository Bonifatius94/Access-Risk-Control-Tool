package data.localdb;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessPatternXAccessConditionMap;
import data.entities.AccessProfileCondition;
import data.entities.Configuration;
import data.entities.ConfigurationXAccessPatternMap;
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

        // configs
        list.add(Configuration.class);
        list.add(ConfigurationXAccessPatternMap.class);
        list.add(AccessPattern.class);
        list.add(AccessPatternXAccessConditionMap.class);
        list.add(AccessCondition.class);
        list.add(AccessProfileCondition.class);
        list.add(AccessPatternCondition.class);
        list.add(AccessPatternConditionProperty.class);

        // whitelists
        list.add(Whitelist.class);
        list.add(WhitelistEntry.class);

        return list;
    }

}
