package data.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * This class represents an auth pattern condition.
 * It contains a list of auth pattern condition properties, a condition name and implements the ICondition interface.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
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

    private int id;
    private List<AccessPatternConditionProperty> properties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "condition", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
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
