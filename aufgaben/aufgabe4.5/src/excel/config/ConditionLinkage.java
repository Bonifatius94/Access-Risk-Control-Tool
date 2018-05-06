package excel.config;

/**
 * This enum represents a linkage option of conditions on an auth pattern.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public enum ConditionLinkage {
    And, Or, None;

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        return (this == And) ? "AND" : (this == Or) ? "OR" : "NONE";
    }
}
