package data.entities;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "Whitelists")
public class Whitelist implements IReferenceAware, ICreationFlagsHelper {

    private Integer id;
    private String description;

    private boolean isArchived;
    private ZonedDateTime createdAt;
    private String createdBy;

    private Set<WhitelistEntry> entries = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(columnDefinition = "TEXT", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "whitelist", cascade = CascadeType.ALL, orphanRemoval = true)
    //@Fetch(value = FetchMode.SUBSELECT)
    public Set<WhitelistEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<WhitelistEntry> entries) {
        setEntries(new HashSet<>(entries));
    }

    public void setEntries(Set<WhitelistEntry> entries) {
        this.entries = entries;
        entries.forEach(x -> x.setWhitelist(this));
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
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
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
     * This is a custom implementation of equals method that checks for data equality.
     *
     * @param other the object to compare with
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object other) {

        /*boolean ret = (other == this);

        if (other instanceof Whitelist) {

            Whitelist cmp = (Whitelist) other;

            ret = (this.description.equals(cmp.getDescription())
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
