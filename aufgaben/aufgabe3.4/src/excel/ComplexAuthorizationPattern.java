package excel;

import java.util.List;

public class ComplexAuthorizationPattern extends AuthorizationPattern {

    //public ComplexAuthorizationPattern() { }

    public ComplexAuthorizationPattern(List<ICondition> conditions, ConditionLinkage linkage) {

        setConditions(conditions);
        setLinkage(linkage);
    }

    public ComplexAuthorizationPattern(String name, String description, List<ICondition> conditions, ConditionLinkage linkage) {

        super(name, description);
        setConditions(conditions);
        setLinkage(linkage);
    }

    private List<ICondition> conditions;
    private ConditionLinkage linkage;

    public List<ICondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<ICondition> subPatterns) {
        this.conditions = subPatterns;
    }

    public ConditionLinkage getLinkage() {
        return linkage;
    }

    public void setLinkage(ConditionLinkage linkage) {
        this.linkage = linkage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString()).append(", linkage='").append(linkage.toString()).append("', conditions: ");
        conditions.forEach(x -> builder.append("\r\ncondition: ").append(x.toString()));
        return builder.toString();
    }

}
