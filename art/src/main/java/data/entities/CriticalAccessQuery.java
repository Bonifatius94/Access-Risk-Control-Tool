package data.entities;

import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "CriticalAccessQueries")
public class CriticalAccessQuery implements IReferenceAware, ICreationFlagsHelper {

    private Integer id;
    private Configuration config;
    private SapConfiguration sapConfig;
    private List<CriticalAccessEntry> entries = new ArrayList<>();

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

    @ManyToOne/*(cascade = { CascadeType.PERSIST, CascadeType.MERGE })*/
    @JoinColumn(name = "ConfigId")
    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    @ManyToOne/*(cascade = { CascadeType.PERSIST, CascadeType.MERGE })*/
    @JoinColumn(name = "SapConfigId")
    public SapConfiguration getSapConfig() {
        return sapConfig;
    }

    public void setSapConfig(SapConfiguration sapConfig) {
        this.sapConfig = sapConfig;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "query", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<CriticalAccessEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CriticalAccessEntry> entries) {
        this.entries = entries;
        entries.forEach(x -> x.setQuery(this));
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
        entries.forEach(x -> x.setQuery(this));
    }

    @Override
    public void initCreationFlags(ZonedDateTime createdAt, String createdBy) {

        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
    }

    @Override
    public String toString() {

        // TODO: implement logic
        return super.toString();
    }

}
