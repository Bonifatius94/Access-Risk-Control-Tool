package data.entities.analysis;

import data.entities.config.Configuration;

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
import javax.persistence.Table;
import javax.persistence.Transient;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

@Entity
@Table(name = "analysis.Configurations_History")
public class ConfigurationHistory {

    /**
     * Empty constructor required by hibernate.
     */
    public ConfigurationHistory() {
        // nothing to do here ...
    }

    /**
     * This constructor converts a configuration to configuration history.
     *
     * @param config the original configuration
     */
    public ConfigurationHistory(Configuration config) {

        setDescription(config.getDescription());
        setName(config.getName());
        setWhitelist(new WhitelistHistory(config.getWhitelist()));
        setPatterns(config.getPatterns().stream().map(x -> new AccessPatternHistory(x.getPattern())).collect(Collectors.toList()));
    }

    private Integer id;
    private String name;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConfigurationXAccessPatternMapHistory> patterns;

    private WhitelistHistory whitelist;

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
    public Set<AccessPatternHistory> getPatterns() {
        return patterns.stream().map(x -> x.getPattern()).collect(Collectors.toSet());
    }

    public void setPatterns(Set<ConfigurationXAccessPatternMapHistory> patterns) {
        this.patterns = patterns;
    }

    /**
     * This setter allows to overload access patterns instead of map entries.
     *
     * @param patterns the patters to be set to this instance
     */
    public void setPatterns(List<AccessPatternHistory> patterns) {

        // TODO: check if this logic actually works fine
        setPatterns(patterns.stream().map(x -> new ConfigurationXAccessPatternMapHistory(this, x)).collect(Collectors.toSet()));
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "WhitelistId")
    public WhitelistHistory getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(WhitelistHistory whitelist) {
        this.whitelist = whitelist;
    }

}
