package data.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "Configurations")
public class Configuration implements IReferenceAware {

    private Integer id;
    private String name;
    private String description;
    private List<AccessPattern> patterns = new ArrayList<>();
    private Whitelist whitelist;

    private boolean isArchived;
    private OffsetDateTime createdAt;
    private String createdBy;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    // CAUTION: cascade types ALL or REMOVE lead to cascading deletions on both sides!!!
    // this @ManyToMany setup should only write entries into the mapping table and not into the referenced table
    @ManyToMany
    @JoinTable(
        name = "nm_Configuration_AccessPattern",
        joinColumns = { @JoinColumn(name = "ConfigId") },
        inverseJoinColumns = { @JoinColumn(name = "AccessPatternId") }
    )
    @Fetch(value = FetchMode.SUBSELECT)
    public List<AccessPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<AccessPattern> patterns) {
        this.patterns = patterns;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "WhitelistId")
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // =============================
    //      hibernate triggers
    // =============================

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    // =============================
    //   helpers for foreign keys
    // =============================

    public void addPattern(AccessPattern pattern) {
        patterns.add(pattern);
        pattern.getConfigurations().add(this);
    }

    public void removePattern(AccessPattern pattern) {
        patterns.remove(pattern);
        pattern.getConfigurations().remove(this);
    }

    // =============================
    //          overrides
    // =============================

    /**
     * This method adjusts the foreign key references.
     */
    @Override
    public void adjustReferences() {

        // adjust patterns
        getPatterns().forEach(x -> {
            if (!x.getConfigurations().contains(this)) {
                x.getConfigurations().add(this);
            }
        });
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

        builder.append("Name = ").append(getName()).append(", Description = ").append(getDescription())
            .append("\r\nWhitelist:\r\n").append(getWhitelist())
            .append("\r\nPatterns:");

        getPatterns().forEach(x -> builder.append("\r\n").append(x));

        return builder.toString();
    }

}
