package data.entities;

import java.time.ZonedDateTime;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

/**
 * This class represents an auth pattern (simple or complex).
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "AccessPatterns")
public class AccessPattern implements IReferenceAware, IDataEntity {

    // =============================
    //        constructors
    // =============================

    public AccessPattern() {
        // nothing to do here ...
    }

    /**
     * This constructor creates a new instance of an auth pattern of complex type (without usecase id and description defined).
     *
     * @param conditions a list of conditions applied to this auth pattern
     * @param linkage    the linkage applied to this auth pattern (not none)
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

    /**
     * This constructor creates a new instance of an auth pattern of simple type (without usecase id and description defined).
     *
     * @param condition the condition applied to this auth pattern
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
     */
    public AccessPattern(String usecaseId, String description, AccessCondition condition) {

        setUsecaseId(usecaseId);
        setDescription(description);
        setConditions(Collections.singletonList(condition));
        setLinkage(ConditionLinkage.None);
    }

    /**
     * This constructor clones an existing.
     *
     * @param original the pattern to clone
     */
    public AccessPattern(AccessPattern original) {

        this.setUsecaseId(original.getUsecaseId());
        this.setDescription(original.getDescription());
        this.setLinkage(original.getLinkage());

        this.setConditions(original.getConditions().stream().map(x -> new AccessCondition(x)).collect(Collectors.toSet()));
        //conditions.forEach(x -> x.setPattern(this));
    }

    // =============================
    //           members
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String usecaseId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 4)
    private ConditionLinkage linkage = ConditionLinkage.None;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pattern", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccessCondition> conditions = new HashSet<>();

    @ManyToMany(mappedBy = "patterns", fetch = FetchType.EAGER)
    private Set<Configuration> configurations = new HashSet<>();

    @Column(nullable = false)
    private boolean isArchived;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    // =============================
    //        getter / setter
    // =============================

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

    public Set<AccessCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<AccessCondition> conditions) {
        setConditions(new HashSet<>(conditions));
    }

    /**
     * This setter applies the new conditions while managing to handle foreign key references.
     *
     * @param conditions the conditions to be set
     */
    public void setConditions(Set<AccessCondition> conditions) {

        this.conditions.forEach(x -> x.setPattern(null));
        this.conditions.clear();
        this.conditions.addAll(conditions);
        adjustReferences();
    }

    public ConditionLinkage getLinkage() {
        return linkage;
    }

    public void setLinkage(ConditionLinkage linkage) {
        this.linkage = linkage;
    }

    public Set<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        setConfigurations(new HashSet<>(configurations));
    }

    /**
     * This setter applies the new configurations while managing to handle foreign key references.
     *
     * @param configurations the conditions to be set
     */
    public void setConfigurations(Set<Configuration> configurations) {

        this.configurations.forEach(x -> x.getPatterns().remove(this));
        this.configurations.clear();
        this.configurations.addAll(configurations);
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
        getConfigurations().forEach(x -> x.getPatterns().add(this));
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
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("usecaseId='").append(getUsecaseId()).append("', description='").append(getDescription()).append("'");
        if (linkage != null) {
            builder.append(", linkage='").append(linkage.toString()).append("', conditions: ");
        }
        conditions.forEach(x -> builder.append("\r\ncondition: ").append(x.toString()));
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccessPattern pattern = (AccessPattern) o;
        return Objects.equals(usecaseId, pattern.usecaseId)
            && Objects.equals(description, pattern.description)
            && Objects.equals(createdAt, pattern.createdAt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(usecaseId, description, createdAt);
    }
}
