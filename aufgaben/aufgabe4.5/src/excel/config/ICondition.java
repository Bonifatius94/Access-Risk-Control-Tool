package excel.config;

/**
 * This is an interface defining the operations of an auth pattern condition (profile / pattern condition).
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public interface ICondition {

    /**
     * This method gets the custom condition name of this instance.
     *
     * @return custom condition name of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    String getConditionName();

    /**
     * This method sets the custom condition name of this instance.
     *
     * @param conditionName new custom condition name of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    void setConditionName(String conditionName);

    /**
     * This method gets the condition type of this instance (just for displaying).
     *
     * @return the condition type of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    String getConditionType();
}
