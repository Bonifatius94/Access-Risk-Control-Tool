//package data.entities;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * This class represents an auth pattern (simple or complex).
// *
// * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
// */
//public class AuthorizationPattern {
//
//    // =============================
//    //      complex auth pattern
//    // =============================
//
//    /**
//     * This constructor creates a new instance of an auth pattern of complex type (without usecase id and description defined).
//     *
//     * @param conditions a list of conditions applied to this auth pattern
//     * @param linkage    the linkage applied to this auth pattern (not none)
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public AuthorizationPattern(List<ICondition> conditions, ConditionLinkage linkage) {
//
//        if (linkage == ConditionLinkage.None) {
//            throw new IllegalArgumentException("Linkage of a complex auth pattern must not be none.");
//        }
//
//        setConditions(conditions);
//        setLinkage(linkage);
//    }
//
//    /**
//     * This constructor creates a new instance of an auth pattern of complex type (with usecase id and description defined).
//     *
//     * @param usecaseId   the usecase id applied to this auth pattern
//     * @param description the description applied to this auth pattern
//     * @param conditions  a list of conditions applied to this auth pattern
//     * @param linkage     the linkage applied to this auth pattern (not none)
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public AuthorizationPattern(String usecaseId, String description, List<ICondition> conditions, ConditionLinkage linkage) {
//
//        if (linkage == ConditionLinkage.None) {
//            throw new IllegalArgumentException("Linkage of a complex auth pattern must not be none.");
//        }
//
//        setUsecaseId(usecaseId);
//        setDescription(description);
//        setConditions(conditions);
//        setLinkage(linkage);
//    }
//
//    // =============================
//    //      simple auth pattern
//    // =============================
//
//    /**
//     * This constructor creates a new instance of an auth pattern of simple type (without usecase id and description defined).
//     *
//     * @param condition the condition applied to this auth pattern
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public AuthorizationPattern(ICondition condition) {
//
//        setConditions(Collections.singletonList(condition));
//        setLinkage(ConditionLinkage.None);
//    }
//
//    /**
//     * This constructor creates a new instance of an auth pattern of simple type (without usecase id and description defined).
//     *
//     * @param usecaseId   the usecase id applied to this auth pattern
//     * @param description the description applied to this auth pattern
//     * @param condition   the condition applied to this auth pattern
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public AuthorizationPattern(String usecaseId, String description, ICondition condition) {
//
//        setUsecaseId(usecaseId);
//        setDescription(description);
//        setConditions(Collections.singletonList(condition));
//        setLinkage(ConditionLinkage.None);
//    }
//
//    // =============================
//    //           properties
//    // =============================
//
//    private String usecaseId;
//    private String description;
//    private List<ICondition> conditions;
//    private ConditionLinkage linkage;
//
//    // =============================
//    //        getter / setter
//    // =============================
//
//    /**
//     * This method gets the usecase id of this instance.
//     *
//     * @return the usecase id of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public String getUsecaseId() {
//        return usecaseId;
//    }
//
//    /**
//     * This method sets the new usecase id of this instance.
//     *
//     * @param usecaseId the new usecase id of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public void setUsecaseId(String usecaseId) {
//        this.usecaseId = usecaseId;
//    }
//
//    /**
//     * This method gets the description of this instance.
//     *
//     * @return the description of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public String getDescription() {
//        return description;
//    }
//
//    /**
//     * This method sets the new description of this instance.
//     *
//     * @param description the new description of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    /**
//     * This method gets the list of conditions of this instance (simple type => only one condition in list).
//     *
//     * @return the list of conditions of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public List<ICondition> getConditions() {
//        return conditions;
//    }
//
//    /**
//     * This method sets the new list of conditions of this instance (simple type => only one condition in list).
//     *
//     * @param conditions the new list of conditions of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public void setConditions(List<ICondition> conditions) {
//        this.conditions = conditions;
//    }
//
//    /**
//     * This method gets the linkage of this instance (simple type => always none).
//     *
//     * @return the linkage of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public ConditionLinkage getLinkage() {
//        return linkage;
//    }
//
//    /**
//     * This method sets the new linkage of this instance (simple type => always none).
//     *
//     * @param linkage the new linkage of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public void setLinkage(ConditionLinkage linkage) {
//        this.linkage = linkage;
//    }
//
//    /**
//     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
//     *
//     * @return JSON-like data representation of this instance as a string
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("usecaseId='").append(getUsecaseId()).append("', description='").append(getDescription()).append("'");
//        builder.append(", linkage='").append(linkage.toString()).append("', conditions: ");
//        conditions.forEach(x -> builder.append("\r\ncondition: ").append(x.toString()));
//        return builder.toString();
//    }
//
//}
