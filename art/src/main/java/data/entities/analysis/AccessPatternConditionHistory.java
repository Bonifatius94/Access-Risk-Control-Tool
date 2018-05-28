package data.entities.analysis;

import data.entities.config.AccessPatternCondition;

import java.util.HashSet;
import java.util.List;
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

/**
 * This class represents an auth pattern condition.
 * It contains a list of auth pattern condition properties, a condition name and implements the ICondition interface.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "analysis.AccessPatternConditions_History")
public class AccessPatternConditionHistory {

    /**
     * This is an empty constructor required by hibernate.
     */
    public AccessPatternConditionHistory() {
        // nothing to do here ...
    }

    public AccessPatternConditionHistory(List<AccessPatternConditionPropertyHistory> properties) {
        setProperties(new HashSet<>(properties));
    }

    /**
     * This constructor converts an access pattern condition to an access pattern condition history.
     *
     * @param condition the original pattern condition
     */
    public AccessPatternConditionHistory(AccessPatternCondition condition) {

        setProperties(condition.getProperties().stream().map(x -> new AccessPatternConditionPropertyHistory(x)).collect(Collectors.toSet()));
    }

    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccessPatternConditionPropertyHistory> properties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    public Set<AccessPatternConditionPropertyHistory> getProperties() {
        return properties;
    }

    public void setProperties(Set<AccessPatternConditionPropertyHistory> properties) {
        this.properties = properties;
    }

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("properties: ");
        getProperties().forEach(x -> builder.append("\r\nproperty: ").append(x.toString()));
        return builder.toString();
    }

}
