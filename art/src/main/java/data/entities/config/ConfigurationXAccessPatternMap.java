package data.entities.config;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "config.nm_Configuration_AccessPattern")
public class ConfigurationXAccessPatternMap {

    /**
     * Empty constructor required by hibernate.
     */
    public ConfigurationXAccessPatternMap() {

    }

    /**
     * Constructor for initializing a new map entry with given data.
     *
     * @param config the config to be mapped
     * @param pattern the pattern to be mapped
     */
    public ConfigurationXAccessPatternMap(Configuration config, AccessPattern pattern) {

        setConfig(config);
        setPattern(pattern);
    }

    private Integer id;
    private AccessPattern pattern;
    private Configuration config;

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
    public AccessPattern getPattern() {
        return pattern;
    }

    public void setPattern(AccessPattern pattern) {
        this.pattern = pattern;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ConfigId")
    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

}
