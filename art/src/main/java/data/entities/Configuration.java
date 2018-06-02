package data.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Configurations")
public class Configuration {

    private Integer id;
    private String name;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfigurationXAccessPatternMap> patterns;
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

    @Transient
    public List<AccessPattern> getPatterns() {
        // TODO: test if this code works fine with sap test
        return patterns.stream().map(x -> x.getPattern()).collect(Collectors.toList());
    }

    /*public void setPatterns(List<ConfigurationXAccessPatternMap> patterns) {
        this.patterns = patterns;
    }*/

    /**
     * This setter allows to overload access patterns instead of map entries.
     *
     * @param patterns the patters to be set to this instance
     */
    public void setPatterns(List<AccessPattern> patterns) {

        this.patterns = patterns.stream().map(x -> new ConfigurationXAccessPatternMap(this, x)).collect(Collectors.toList());
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

        builder.append("Name = ").append(getName()).append(", Description = ").append(getDescription())
            .append("\r\nWhitelist:\r\n").append(getWhitelist())
            .append("\r\nPatterns:");

        getPatterns().forEach(x -> builder.append("\r\n").append(x));

        return builder.toString();
    }

}
