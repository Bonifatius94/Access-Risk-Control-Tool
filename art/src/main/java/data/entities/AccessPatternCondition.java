package data.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * This class represents an auth pattern condition.
 * It contains a list of auth pattern condition properties, a condition name and implements the ICondition interface.
 */
@Entity
@Table(name = "AccessPatternConditions")
public class AccessPatternCondition implements IReferenceAware {

    // empty constructor is required for hibernate
    public AccessPatternCondition() {
        // nothing to do here ...
    }

    public AccessPatternCondition(List<AccessPatternConditionProperty> properties) {
        setProperties(new ArrayList<>(properties));
    }

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @MapsId
    private AccessCondition condition;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "condition", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccessPatternConditionProperty> properties = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccessCondition getCondition() {
        return condition;
    }

    public void setCondition(AccessCondition condition) {
        this.condition = condition;
    }

    public Set<AccessPatternConditionProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<AccessPatternConditionProperty> properties) {
        setProperties(new HashSet<>(properties));
    }

    /**
     * This setter applies the new properties while managing to handle foreign key references.
     *
     * @param properties the conditions to be set
     */
    public void setProperties(Set<AccessPatternConditionProperty> properties) {

        this.properties.forEach(x -> x.setCondition(null));
        this.properties.clear();
        this.properties.addAll(properties);
        adjustReferences();
    }

    // =============================
    //          overrides
    // =============================

    /**
     * This method adjusts the foreign key references.
     */
    @Override
    public void adjustReferences() {

        // adjust properties
        getProperties().forEach(x -> x.setCondition(this));
    }

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("properties: ");
        getProperties().forEach(x -> builder.append("\r\nproperty: ").append(x.toString()));
        return builder.toString();
    }

}
