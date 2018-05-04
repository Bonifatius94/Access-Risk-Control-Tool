package excel.config;

import java.util.Collections;
import java.util.List;

public class AuthorizationPattern {

    // =============================
    //      complex auth pattern
    // =============================

    public AuthorizationPattern(List<ICondition> conditions, ConditionLinkage linkage) {

        setConditions(conditions);
        setLinkage(linkage);
    }

    public AuthorizationPattern(String usecaseID, String description, List<ICondition> conditions, ConditionLinkage linkage) {

        setUsecaseID(usecaseID);
        setDescription(description);
        setConditions(conditions);
        setLinkage(linkage);
    }

    // =============================
    //      simple auth pattern
    // =============================

    public AuthorizationPattern(ICondition condition) {

        setConditions(Collections.singletonList(condition));
        setLinkage(ConditionLinkage.None);
    }

    public AuthorizationPattern(String usecaseID, String description, ICondition condition) {

        setUsecaseID(usecaseID);
        setDescription(description);
        setConditions(Collections.singletonList(condition));
        setLinkage(ConditionLinkage.None);
    }

    // =============================
    //           properties
    // =============================

    private String usecaseID;
    private String description;
    private List<ICondition> conditions;
    private ConditionLinkage linkage;

    // =============================
    //        getter / setter
    // =============================

    public String getUsecaseID() {
        return usecaseID;
    }

    public void setUsecaseID(String usecaseID) {
        this.usecaseID = usecaseID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
        builder.append("usecaseID='").append(getUsecaseID()).append("', description='").append(getDescription()).append("'");
        builder.append(", linkage='").append(linkage.toString()).append("', conditions: ");
        conditions.forEach(x -> builder.append("\r\ncondition: ").append(x.toString()));
        return builder.toString();
    }

}
