package data.entities.analysis;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

// TODO: rename this class to "query" instead of "list"
@Entity
@Table(name = "analysis.CriticalAccessLists")
public class CriticalAccessList {

    // empty constructor required by hibernate
    public CriticalAccessList() {
        // nothing to do here ...
    }

    // TODO: remove this contructor because it is never used
    public CriticalAccessList(Set<CriticalAccessEntry> list) {
        this.entries = list;
    }

    // TODO: add reference to the whitelist used for the query
    // TODO: add reference to the config used for the query

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CriticalAccessEntry> entries = new HashSet<>();

    @Transient
    public Set<CriticalAccessEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<CriticalAccessEntry> entries) {
        this.entries = entries;
    }
}
