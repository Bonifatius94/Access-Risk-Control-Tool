package data.entities;

import java.time.ZonedDateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Configurations")
public class Configuration implements IReferenceAware, IDataEntity {

    // =============================
    //         constructors
    // =============================

    public Configuration() {
        // nothing to do here ...
    }

    /**
     * This constructor initializes an instance with the given parameters.
     *
     * @param name the name of the new instance
     * @param description the description of the new instance
     * @param patterns the patterns referenced by the new instance
     * @param whitelist the whitelist referenced by the new instance
     */
    public Configuration(String name, String description, List<AccessPattern> patterns, Whitelist whitelist) {

        setName(name);
        setDescription(description);
        setPatterns(patterns);
        setWhitelist(whitelist);
    }

    /**
     * This constructor clones the given config (patterns and whitelist are not cloned).
     *
     * @param original the config to clone
     */
    public Configuration(Configuration original) {

        this.setName(original.getName());
        this.setDescription(original.getDescription());
    }

    // =============================
    //           members
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // CAUTION: cascade types ALL or REMOVE lead to cascading deletions on both sides!!!
    // this @ManyToMany setup should only write entries into the mapping table and not into the referenced table
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "nm_Configuration_AccessPattern",
        joinColumns = { @JoinColumn(name = "ConfigId") },
        inverseJoinColumns = { @JoinColumn(name = "AccessPatternId") }
    )
    private Set<AccessPattern> patterns = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "WhitelistId")
    private Whitelist whitelist;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean isArchived;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    // =============================
    //      getters / setters
    // =============================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<AccessPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<AccessPattern> patterns) {
        setPatterns(new HashSet<>(patterns));
    }

    /**
     * This setter applies the new patterns while managing to handle foreign key references.
     *
     * @param patterns the conditions to be set
     */
    public void setPatterns(Set<AccessPattern> patterns) {

        this.patterns.forEach(x -> x.getConfigurations().remove(this));
        this.patterns.clear();
        this.patterns.addAll(patterns);
        adjustReferences();
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
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

    /*// =============================
    //   helpers for foreign keys
    // =============================

    public void addPattern(AccessPattern pattern) {
        patterns.add(pattern);
        pattern.getConfigurations().add(this);
    }

    public void removePattern(AccessPattern pattern) {
        patterns.remove(pattern);
        pattern.getConfigurations().remove(this);
    }*/

    // =============================
    //          overrides
    // =============================

    /**
     * This method adjusts the foreign key references.
     */
    @Override
    public void adjustReferences() {

        // adjust patterns
        getPatterns().forEach(x -> x.getConfigurations().add(this));
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

        builder.append("Name = ").append(getName()).append(", Description = ").append(getDescription())
            .append("\r\nWhitelist:\r\n").append(getWhitelist())
            .append("\r\nPatterns:");

        getPatterns().forEach(x -> builder.append("\r\n").append(x));

        return builder.toString();
    }

}
