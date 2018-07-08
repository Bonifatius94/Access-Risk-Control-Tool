package data.entities;

/**
 * This enum represents a linkage option of conditions on an auth pattern.
 */
public enum ConditionLinkage {
    And, Or, None;

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     */
    @Override
    public String toString() {

        //Enum.valueOf(this )

        switch (this) {
            case And:  return "And";
            case Or:   return "Or";
            case None: return "None";
            default:   throw new IllegalArgumentException("Unknown ConditionLinkage type!");
        }
    }
}
