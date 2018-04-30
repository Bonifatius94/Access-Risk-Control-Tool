package excel.config;

import java.util.List;

public class AuthorizationPatternCondition implements ICondition {

    public AuthorizationPatternCondition(List<AuthorizationPatternConditionProperty> properties) {

        setProperties(properties);
    }

    private String conditionName;
    private List<AuthorizationPatternConditionProperty> properties;

    public List<AuthorizationPatternConditionProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<AuthorizationPatternConditionProperty> properties) {
        this.properties = properties;
    }

    @Override
    public String getConditionName() {
        return conditionName;
    }

    @Override
    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    @Override
    public String getConditionType() {
        return "Auth Pattern";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("properties: ");
        getProperties().forEach(x -> builder.append("\r\nproperty: ").append(x.toString()));
        return builder.toString();
    }

}
