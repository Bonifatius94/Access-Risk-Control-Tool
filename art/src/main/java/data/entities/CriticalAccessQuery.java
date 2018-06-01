package data.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    //public CriticalAccessQuery(Set<CriticalAccessEntry> list) {
    //    this.entries = list;
    //}

    private Integer id;
    private Configuration config;
    private SapConfiguration sapConfig;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CriticalAccessEntry> entries = new HashSet<>();

    private boolean isArchived;

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
    public Set<CriticalAccessEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<CriticalAccessEntry> entries) {
        this.entries = entries;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

}
