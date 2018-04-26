package excel;

import java.util.List;

public class AuthorizationPatternCondition implements ICondition {

    public AuthorizationPatternCondition(List<AuthorizationPatternConditionProperty> properties) {

        setProperties(properties);
    }

    private List<AuthorizationPatternConditionProperty> properties;

    public List<AuthorizationPatternConditionProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<AuthorizationPatternConditionProperty> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("properties: ");
        getProperties().forEach(x -> builder.append("\r\nproperty: ").append(x.toString()));
        return builder.toString();
    }

}
