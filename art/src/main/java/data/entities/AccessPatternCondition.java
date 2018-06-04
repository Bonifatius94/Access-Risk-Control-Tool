package data.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * This class represents an auth pattern condition.
 * It contains a list of auth pattern condition properties, a condition name and implements the ICondition interface.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "AccessPatternConditions")
public class AccessPatternCondition {

    // empty constructor is required for hibernate
    public AccessPatternCondition() {
        // nothing to do here ...
    }

    public AccessPatternCondition(List<AccessPatternConditionProperty> properties) {
        setProperties(new ArrayList<>(properties));
    }

    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccessPatternConditionProperty> properties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    public List<AccessPatternConditionProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<AccessPatternConditionProperty> properties) {
        this.properties = properties;
    }

    // =============================
    //          overrides
    // =============================

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
