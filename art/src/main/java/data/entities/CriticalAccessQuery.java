package data.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "CriticalAccessQueries")
public class CriticalAccessQuery {

    ///**
    // * empty constructor required by hibernate
    // */
    //public CriticalAccessQuery() {
    //    // nothing to do here ...
    //}

    // TODO: remove this contructor because it is never used
    //public CriticalAccessQuery(Set<CriticalAccessQueryEntry> list) {
    //    this.entries = list;
    //}

    private Integer id;
    private Configuration config;
    private SapConfiguration sapConfig;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CriticalAccessQueryEntry> entries = new ArrayList<>();

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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ConfigId")
    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "SapConfigId")
    public SapConfiguration getSapConfig() {
        return sapConfig;
    }

    public void setSapConfig(SapConfiguration sapConfig) {
        this.sapConfig = sapConfig;
    }

    @Transient
    public List<CriticalAccessQueryEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CriticalAccessQueryEntry> entries) {
        this.entries = entries;
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

}
