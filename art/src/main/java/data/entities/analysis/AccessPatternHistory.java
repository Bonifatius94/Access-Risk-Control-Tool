package data.entities.analysis;

import data.entities.ConditionLinkage;
import data.entities.config.AccessCondition;
import data.entities.config.AccessPattern;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

/**
 * This class represents an auth pattern (simple or complex).
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "analysis.AccessPatterns_History")
public class AccessPatternHistory {

    /**
     * Empty constructor reuqired by hibernate.
     */
    public AccessPatternHistory() {
        // nothing to do here ...
    }

    // =============================
    //      complex auth pattern
    // =============================

    /**
     * This constructor creates a new instance of an auth pattern of complex type (without usecase id and description defined).
     *
     * @param conditions a list of conditions applied to this auth pattern
     * @param linkage    the linkage applied to this auth pattern (not none)
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AccessPatternHistory(List<AccessConditionHistory> conditions, ConditionLinkage linkage) {

        if (linkage == ConditionLinkage.None) {
            throw new IllegalArgumentException("Linkage of a complex auth pattern must not be none.");
        }

        setConditions(new HashSet<>(conditions));
        setLinkage(linkage);
    }

    /**
     * This constructor creates a new instance of an auth pattern of complex type (with usecase id and description defined).
     *
     * @param usecaseId   the usecase id applied to this auth pattern
     * @param description the description applied to this auth pattern
     * @param conditions  a list of conditions applied to this auth pattern
     * @param linkage     the linkage applied to this auth pattern (not none)
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AccessPatternHistory(String usecaseId, String description, List<AccessConditionHistory> conditions, ConditionLinkage linkage) {

        if (linkage == ConditionLinkage.None) {
            throw new IllegalArgumentException("Linkage of a complex auth pattern must not be none.");
        }

        setUsecaseId(usecaseId);
        setDescription(description);
        setConditions(new HashSet<>(conditions));
        setLinkage(linkage);
    }

    // =============================
    //      simple auth pattern
    // =============================

    /**
     * This constructor creates a new instance of an auth pattern of simple type (without usecase id and description defined).
     *
     * @param condition the condition applied to this auth pattern
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AccessPatternHistory(AccessConditionHistory condition) {

        setConditions(new HashSet<>(Collections.singletonList(condition)));
        setLinkage(ConditionLinkage.None);
    }

    /**
     * This constructor creates a new instance of an auth pattern of simple type (without usecase id and description defined).
     *
     * @param usecaseId   the usecase id applied to this auth pattern
     * @param description the description applied to this auth pattern
     * @param condition   the condition applied to this auth pattern
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AccessPatternHistory(String usecaseId, String description, AccessConditionHistory condition) {

        setUsecaseId(usecaseId);
        setDescription(description);
        setConditions(new HashSet<>(Collections.singletonList(condition)));
        setLinkage(ConditionLinkage.None);
    }

    /**
     * This constructor converts an access pattern to an access pattern history.
     *
     * @param pattern the original pattern
     */
    public AccessPatternHistory(AccessPattern pattern) {

        // TODO: implement n-m mapping
        setConditions(pattern.getConditions().stream().map(x -> new AccessConditionHistory(x)).collect(Collectors.toSet()));
        //getConditions().forEach(x -> x.set);
    }

    // =============================
    //           properties
    // =============================

    // TODO: try to use @ManyToMany annotation for n-m relation (see: http://www.baeldung.com/hibernate-many-to-many)

    private Integer id;
    private String usecaseId;
    private String description;
    private ConditionLinkage linkage = ConditionLinkage.None;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccessConditionHistory> conditions;

    // =============================
    //        getter / setter
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public Set<AccessConditionHistory> getConditions() {
        return conditions;
    }

    public void setConditions(Set<AccessConditionHistory> conditions) {
        this.conditions = conditions;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 4)
    public ConditionLinkage getLinkage() {
        return linkage;
    }

    public void setLinkage(ConditionLinkage linkage) {
        this.linkage = linkage;
    }

    // =============================
    //          overrides
    // =============================

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("usecaseId='").append(getUsecaseId()).append("', description='").append(getDescription()).append("'");
        builder.append(", linkage='").append(linkage.toString()).append("', conditions: ");
        conditions.forEach(x -> builder.append("\r\ncondition: ").append(x.toString()));
        return builder.toString();
    }

}
