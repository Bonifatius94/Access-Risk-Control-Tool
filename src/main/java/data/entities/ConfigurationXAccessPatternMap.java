//package data.entities;
//
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "nm_Configuration_AccessPattern")
//public class ConfigurationXAccessPatternMap {
//
//    /**
//     * Empty constructor required by hibernate.
//     */
//    public ConfigurationXAccessPatternMap() {
//
//    }
//
//    /**
//     * Constructor for initializing a new map entry with given data.
//     *
//     * @param config the config to be mapped
//     * @param pattern the pattern to be mapped
//     */
//    public ConfigurationXAccessPatternMap(Configuration config, AccessPattern pattern) {
//
//        setConfig(config);
//        setPattern(pattern);
//    }
//
//    /*@Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;*/
//
//    @ManyToOne/*(cascade = { CascadeType.PERSIST, CascadeType.MERGE })*/
//    @JoinColumn(name = "AccessPatternId")
//    private AccessPattern pattern;
//
//    @ManyToOne/*(cascade = { CascadeType.PERSIST, CascadeType.MERGE })*/
//    @JoinColumn(name = "ConfigId")
//    private Configuration config;
//
//    /*public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }*/
//
//    public AccessPattern getPattern() {
//        return pattern;
//    }
//
//    public void setPattern(AccessPattern pattern) {
//        this.pattern = pattern;
//    }
//
//    public Configuration getConfig() {
//        return config;
//    }
//
//    public void setConfig(Configuration config) {
//        this.config = config;
//    }
//
//}
