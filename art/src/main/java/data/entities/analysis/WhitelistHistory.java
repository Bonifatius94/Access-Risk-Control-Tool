package data.entities.analysis;

import data.entities.config.Whitelist;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

@Entity
@Table(name = "analysis.Whitelists_History")
public class WhitelistHistory {

    /**
     * Empty constructor required by hibernate.
     */
    public WhitelistHistory() {

    }

    /**
     * This constructor converts a whitelist to a whitelist history.
     *
     * @param whitelist the original whitelist
     */
    public WhitelistHistory(Whitelist whitelist) {

        setDescription(whitelist.getDescription());
        setEntries(whitelist.getEntries().stream().map(x -> new WhitelistEntryHistory(x)).collect(Collectors.toSet()));
        getEntries().forEach(x -> x.setWhitelist(this));
    }

    private Integer id;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WhitelistEntryHistory> entries;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public Set<WhitelistEntryHistory> getEntries() {
        return entries;
    }

    public void setEntries(Set<WhitelistEntryHistory> entries) {
        this.entries = entries;
    }
}
