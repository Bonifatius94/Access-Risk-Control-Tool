package data.entities;

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
@Table(name = "art.AuthPatternConditions")
public class AuthPatternCondition {

    public AuthPatternCondition() {
        // nothing to do here ...
    }

    public AuthPatternCondition(List<AuthPatternConditionProperty> properties) {
        setProperties(new HashSet<>(properties));
    }

    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuthPatternConditionProperty> properties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    public Set<AuthPatternConditionProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<AuthPatternConditionProperty> properties) {
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
