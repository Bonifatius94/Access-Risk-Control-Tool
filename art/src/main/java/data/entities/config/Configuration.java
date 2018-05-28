package data.entities.config;

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

@Entity
@Table(name = "config.Configurations")
public class Configuration {

    private Integer id;
    private String name;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConfigurationXAccessPatternMap> patterns;
    private Whitelist whitelist;

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
    public Set<ConfigurationXAccessPatternMap> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<ConfigurationXAccessPatternMap> patterns) {
        this.patterns = patterns;
    }

    /**
     * This setter allows to overload access patterns instead of map entries.
     *
     * @param patterns the patters to be set to this instance
     */
    public void setPatterns(List<AccessPattern> patterns) {

        setPatterns(patterns.stream().map(x -> new ConfigurationXAccessPatternMap(this, x)).collect(Collectors.toSet()));
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "WhitelistId")
    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
    }

}
