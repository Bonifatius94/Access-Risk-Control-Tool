package data.localdb;

import data.entities.AuthCondition;
import data.entities.AuthPattern;
import data.entities.AuthPatternCondition;
import data.entities.AuthPatternConditionProperty;
import data.entities.AuthPatternXAuthConditionMap;
import data.entities.AuthProfileCondition;
import data.entities.Configuration;
import data.entities.ConfigurationXAuthPatternMap;
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
        list.add(ConfigurationXAuthPatternMap.class);
        list.add(AuthPattern.class);
        list.add(AuthPatternXAuthConditionMap.class);
        list.add(AuthCondition.class);
        list.add(AuthProfileCondition.class);
        list.add(AuthPatternCondition.class);
        list.add(AuthPatternConditionProperty.class);

        // whitelists
        list.add(Whitelist.class);
        list.add(WhitelistEntry.class);

        return list;
    }

}
