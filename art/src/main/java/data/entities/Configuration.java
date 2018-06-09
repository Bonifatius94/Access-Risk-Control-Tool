package data.entities;

import java.time.ZonedDateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "Configurations")
public class Configuration implements IReferenceAware, ICreationFlagsHelper {

    private Integer id;
    private String name;
    private String description;
    private Set<AccessPattern> patterns = new HashSet<>();
    private Whitelist whitelist;

    private boolean isArchived;
    private ZonedDateTime createdAt;
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
    public Set<AccessPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<AccessPattern> patterns) {
        setPatterns(new HashSet<>(patterns));
    }

    public void setPatterns(Set<AccessPattern> patterns) {
        this.patterns = patterns;
        adjustReferences();
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

        builder.append("Name = ").append(getName()).append(", Description = ").append(getDescription())
            .append("\r\nWhitelist:\r\n").append(getWhitelist())
            .append("\r\nPatterns:");

        getPatterns().forEach(x -> builder.append("\r\n").append(x));

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

        if (other instanceof Configuration) {

            Configuration cmp = (Configuration) other;

            ret = (name.equals(cmp.getName())
                && ((this.description == null && cmp.getDescription() == null) || (this.description != null && this.description.equals(cmp.getDescription())))
                && ((this.whitelist == null && cmp.getWhitelist() == null) || (this.whitelist != null && this.whitelist.equals(cmp.getWhitelist())))
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
