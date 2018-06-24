package data.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents an auth pattern condition property.
 */
@Entity
@Table(name = "AccessPatternConditionProperties")
public class AccessPatternConditionProperty {

    // =============================
    //         constructors
    // =============================

    public AccessPatternConditionProperty() {
        // nothing to do here ...
    }

    /**
     * This constructor creates a new instance of an auth pattern condition property.
     *
     * @param authObject         the new auth object of this instance
     * @param authObjectProperty the new auth object property of this instance
     * @param value1             the new first value of this instance
     * @param value2             the new second value of this instance
     * @param value3             the new third value of this instance
     * @param value4             the new fourth value of this instance
     */
    public AccessPatternConditionProperty(String authObject, String authObjectProperty, String value1, String value2, String value3, String value4) {

        setAuthObject(authObject);
        setAuthObjectProperty(authObjectProperty);
        setValue1(value1);
        setValue2(value2);
        setValue3(value3);
        setValue4(value4);
    }

    /**
     * This constructor clone the given instance.
     *
     * @param original the instance to be cloned
     */
    public AccessPatternConditionProperty(AccessPatternConditionProperty original) {

        this.setAuthObject(original.getAuthObject());
        this.setAuthObjectProperty(original.getAuthObjectProperty());
        this.setValue1(original.getValue1());
        this.setValue2(original.getValue2());
        this.setValue3(original.getValue3());
        this.setValue4(original.getValue4());
    }

    // =============================
    //           members
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ConditionId")
    private AccessPatternCondition condition;

    @Column(nullable = false)
    private String authObject;

    @Column(nullable = false)
    private String authObjectProperty;

    @Column(nullable = false)
    private String value1;

    private String value2;
    private String value3;
    private String value4;

    // =============================
    //      getters / setters
    // =============================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccessPatternCondition getCondition() {
        return condition;
    }

    public void setCondition(AccessPatternCondition condition) {
        this.condition = condition;
    }

    public String getAuthObject() {
        return authObject;
    }

    public void setAuthObject(String authObject) {
        this.authObject = authObject;
    }

    public String getAuthObjectProperty() {
        return authObjectProperty;
    }

    public void setAuthObjectProperty(String authObjectProperty) {
        this.authObjectProperty = authObjectProperty;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    // =============================
    //          overrides
    // =============================

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     */
    @Override
    public String toString() {
        return
                    "authObject='" + getAuthObject() + "'"
                + ", property='" + getAuthObjectProperty() + "'"
                + ", value1='" + getValue1() + "'"
                + ", value2='" + getValue2() + "'"
                + ", value3='" + getValue3() + "'"
                + ", value4='" + getValue4() + "'";
    }

}
