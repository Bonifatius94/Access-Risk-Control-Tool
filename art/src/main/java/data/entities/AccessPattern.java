package data.entities;

import java.time.ZonedDateTime;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * This class represents an auth pattern (simple or complex).
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "AccessPatterns")
public class AccessPattern implements IReferenceAware, ICreationFlagsHelper {

    // =============================
    //      empty constructor
    // =============================

    public AccessPattern() {
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
    public AccessPattern(List<AccessCondition> conditions, ConditionLinkage linkage) {

        if (linkage == ConditionLinkage.None) {
            throw new IllegalArgumentException("Linkage of a complex auth pattern must not be none.");
        }

        setConditions(conditions);
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
    public AccessPattern(String usecaseId, String description, List<AccessCondition> conditions, ConditionLinkage linkage) {

        if (linkage == ConditionLinkage.None) {
            throw new IllegalArgumentException("Linkage of a complex auth pattern must not be none.");
        }

        setUsecaseId(usecaseId);
        setDescription(description);
        setConditions(conditions);
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
    public AccessPattern(AccessCondition condition) {

        setConditions(Collections.singletonList(condition));
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
    public AccessPattern(String usecaseId, String description, AccessCondition condition) {

        setUsecaseId(usecaseId);
        setDescription(description);
        setConditions(Collections.singletonList(condition));
        setLinkage(ConditionLinkage.None);
    }

    // =============================
    //           properties
    // =============================

    private Integer id;
    private String usecaseId;
    private String description;
    private ConditionLinkage linkage = ConditionLinkage.None;

    private Set<AccessCondition> conditions = new HashSet<>();
    private Set<Configuration> configurations = new HashSet<>();

    private boolean isArchived;
    private ZonedDateTime createdAt;
    private String createdBy;

    // =============================
    //        getter / setter
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pattern", cascade = CascadeType.ALL, orphanRemoval = true)
    //@Fetch(value = FetchMode.SUBSELECT)
    public Set<AccessCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<AccessCondition> conditions) {
        setConditions(new HashSet<>(conditions));
    }

    public void setConditions(Set<AccessCondition> conditions) {
        this.conditions = conditions;
        adjustReferences();
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 4)
    public ConditionLinkage getLinkage() {
        return linkage;
    }

    public void setLinkage(ConditionLinkage linkage) {
        this.linkage = linkage;
    }

    @ManyToMany(mappedBy = "patterns")
    public Set<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        setConfigurations(new HashSet<>(configurations));
    }

    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
        adjustReferences();
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // =============================
    //          overrides
    // =============================

    /**
     * This method adjusts the foreign key references.
     */
    @Override
    public void adjustReferences() {

        // adjust conditions
        getConditions().forEach(x -> x.setPattern(this));

        // adjust configurations
        getConfigurations().forEach(x -> {
            if (!x.getPatterns().contains(this)) {
                x.getPatterns().add(this);
            }
        });
    }

    @Override
    public void initCreationFlags(ZonedDateTime createdAt, String createdBy) {

        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
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
        builder.append("usecaseId='").append(getUsecaseId()).append("', description='").append(getDescription()).append("'");
        builder.append(", linkage='").append(linkage.toString()).append("', conditions: ");
        conditions.forEach(x -> builder.append("\r\ncondition: ").append(x.toString()));
        return builder.toString();
    }

    /**
     * This is a custom implementation of equals method that checks for data equality.
     *
     * @param other the object to compare with
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object other) {

        /*boolean ret = (other == this);

        if (other instanceof AccessPattern) {

            AccessPattern cmp = (AccessPattern) other;

            ret = (usecaseId.equals(cmp.getUsecaseId())
                && ((this.description == null && cmp.getDescription() == null) || (this.description != null && this.description.equals(cmp.getDescription())))
                && this.linkage == cmp.getLinkage()
                && this.id == null || (
                    this.isArchived == cmp.isArchived()
                    && this.createdAt.equals(cmp.getCreatedAt())
                    && this.createdBy.equals(cmp.getCreatedBy())
                ));
        }

        return ret;*/

        return super.equals(other);
    }

    @Override
    public int hashCode() {
        //return (id != null) ? id : 0;
        return super.hashCode();
    }

}
