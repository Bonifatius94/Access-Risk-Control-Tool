package excel.config;

import java.util.List;

/**
 * This class represents an auth pattern condition.
 * It contains a list of auth pattern condition properties, a condition name and implements the ICondition interface.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class AuthorizationPatternCondition implements ICondition {

    /**
     * This is a constructor that creates a new representation of an auth pattern condition.
     *
     * @param properties a list of auth pattern condition properties
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AuthorizationPatternCondition(List<AuthorizationPatternConditionProperty> properties) {

        setProperties(properties);
    }

    private String conditionName = "<name missing>";
    private List<AuthorizationPatternConditionProperty> properties;

    /** This method gets the auth pattern condition properties of this instance.
     *
     * @return the auth pattern condition properties of this instance.
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public List<AuthorizationPatternConditionProperty> getProperties() {
        return properties;
    }

    /**
     * This method sets the auth pattern condition properties applied to this instance.
     *
     * @param properties the new auth pattern condition properties list of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setProperties(List<AuthorizationPatternConditionProperty> properties) {
        this.properties = properties;
    }

    /**
     * This method gets the custom condition name of this instance (implements method specified in ICondition interface).
     *
     * @return the custom condition name of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String getConditionName() {
        return conditionName;
    }

    /**
     * This method sets the custom condition name of this instance (implements method specified in ICondition interface).
     *
     * @param conditionName new custom condition name of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    /**
     * This method gets the condition type of this instance (implements the method specified in ICondition interface).
     *
     * @return the condition type of this instance (just for displaying purposes)
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String getConditionType() {
        return "Auth Pattern";
    }

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("properties: ");
        getProperties().forEach(x -> builder.append("\r\nproperty: ").append(x.toString()));
        return builder.toString();
    }

}
