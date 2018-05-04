package excel.config;

public enum ConditionLinkage {
    And, Or, None;

    @Override
    public String toString() {
        return (this == And) ? "AND" : (this == Or) ? "OR" : "NONE";
    }
}
