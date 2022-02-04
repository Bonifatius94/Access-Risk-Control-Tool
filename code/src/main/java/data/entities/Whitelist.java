package data.entities;

import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Whitelists")
public class Whitelist implements IReferenceAware, IDataEntity {

    // =============================
    //         constructors
    // =============================

    public Whitelist() {
        // nothing to do here ...
    }

    /**
     * This constructor creates a new instance with the given parameters.
     *
     * @param name        the name of the new instance
     * @param description the description of the new instance
     * @param entries     the whitelist entries of the new instance
     */
    public Whitelist(String name, String description, List<WhitelistEntry> entries) {

        setName(name);
        setDescription(description);
        setEntries(entries);
    }

    /**
     * This constructor clones the given instance.
     *
     * @param original the instance to be cloned
     */
    public Whitelist(Whitelist original) {

        this.setName(original.getName());
        this.setDescription(original.getDescription());
        this.setEntries(original.getEntries().stream().map(x -> new WhitelistEntry(x)).collect(Collectors.toSet()));
    }

    // =============================
    //           members
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "whitelist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WhitelistEntry> entries = new LinkedHashSet<>();

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

    public Set<WhitelistEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<WhitelistEntry> entries) {
        setEntries(new LinkedHashSet<>(entries));
    }

    /**
     * This setter applies the new entries while managing to handle foreign key references.
     *
     * @param entries the conditions to be set
     */
    public void setEntries(Set<WhitelistEntry> entries) {

        this.entries.forEach(x -> x.setWhitelist(null));
        this.entries.clear();
        this.entries.addAll(entries);
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

        // adjust entries
        entries.forEach(x -> x.setWhitelist(this));
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

        builder.append("Description = '").append(getDescription()).append("', Entries:");
        getEntries().forEach(x -> builder.append("\r\n").append(x));
        builder.append("\r\nCreatedAt = ").append(getCreatedAt()).append(", CreatedBy = ").append(createdBy).append(", IsArchived = ").append(isArchived());

        return builder.toString();
    }

    /**
     * This functions tests if to whitelist are equal(id , description , name and entries).
     *
     * @param whitelist the on equality tested whitelist.
     * @return true if the Whitelists are equal, if they are not than it returns false.
     */
    public boolean equals(Whitelist whitelist) {
        if (whitelist == this) {
            return true;
        }
        if (whitelist.getEntries().containsAll(this.getEntries()) && whitelist.getName().equals(this.getName()) && whitelist.getDescription().equals(this.getDescription())
            && whitelist.getId().equals(this.getId()) && this.getCreatedBy().equals(whitelist.getCreatedBy())) {
            return true;
        } else {
            return false;
        }
    }

}
