package data.entities.analysis;

import data.entities.config.ConfigurationXAccessPatternMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

@Entity
@Table(name = "analysis.nm_Configuration_AccessPattern_History")
public class ConfigurationXAccessPatternMapHistory {

    /**
     * Empty constructor required by hibernate.
     */
    public ConfigurationXAccessPatternMapHistory() {

    }

    /**
     * Constructor for initializing a new map entry with given data.
     *
     * @param config the config to be mapped
     * @param pattern the pattern to be mapped
     */
    public ConfigurationXAccessPatternMapHistory(ConfigurationHistory config, AccessPatternHistory pattern) {

        setConfig(config);
        setPattern(pattern);
    }

    private Integer id;
    private AccessPatternHistory pattern;
    private ConfigurationHistory config;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "AccessPatternId")
    public AccessPatternHistory getPattern() {
        return pattern;
    }

    public void setPattern(AccessPatternHistory pattern) {
        this.pattern = pattern;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ConfigId")
    public ConfigurationHistory getConfig() {
        return config;
    }

    public void setConfig(ConfigurationHistory config) {
        this.config = config;
    }

}
