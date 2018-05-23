package data.entities;

/**
 * This class represents an auth pattern condition property.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class AuthorizationPatternConditionProperty {

    /**
     * This constructor creates a new instance of an auth pattern condition property.
     *
     * @param authObject         the new auth object of this instance
     * @param authObjectProperty the new auth object property of this instance
     * @param value1             the new first value of this instance
     * @param value2             the new second value of this instance
     * @param value3             the new third value of this instance
     * @param value4             the new fourth value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AuthorizationPatternConditionProperty(String authObject, String authObjectProperty, String value1, String value2, String value3, String value4) {

        setAuthObject(authObject);
        setAuthObjectProperty(authObjectProperty);
        setValue1(value1);
        setValue2(value2);
        setValue3(value3);
        setValue4(value4);
    }

    private String authObject;
    private String authObjectProperty;
    private String value1;
    private String value2;
    private String value3;
    private String value4;

    /**
     * This method gets the auth object of this instance.
     *
     * @return the auth object of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getAuthObject() {
        return authObject;
    }

    /**
     * This method sets the new auth object of this instance.
     *
     * @param authObject the new auth object of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setAuthObject(String authObject) {
        this.authObject = authObject;
    }

    /**
     * This method gets the auth object property of this instance.
     *
     * @return the auth object property of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getAuthObjectProperty() {
        return authObjectProperty;
    }

    /**
     * This method sets the new auth object property of this instance.
     *
     * @param authObjectProperty the new auth object property of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setAuthObjectProperty(String authObjectProperty) {
        this.authObjectProperty = authObjectProperty;
    }

    /**
     * This method gets the first value of this instance.
     *
     * @return the first value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getValue1() {
        return value1;
    }

    /**
     * This method sets the new first value of this instance.
     *
     * @param value1 the new first value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setValue1(String value1) {
        this.value1 = value1;
    }

    /**
     * This method gets the second value of this instance.
     *
     * @return the second value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getValue2() {
        return value2;
    }

    /**
     * This method sets the new second value of this instance.
     *
     * @param value2 the new second value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setValue2(String value2) {
        this.value2 = value2;
    }

    /**
     * This method gets the third value of this instance.
     *
     * @return the third value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getValue3() {
        return value3;
    }

    /**
     * This method sets the new third value of this instance.
     *
     * @param value3 the new third value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setValue3(String value3) {
        this.value3 = value3;
    }

    /**
     * This method gets the fourth value of this instance.
     *
     * @return the fourth value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getValue4() {
        return value4;
    }

    /**
     * This method sets the new fourth value of this instance.
     *
     * @param value4 the new fourth value of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setValue4(String value4) {
        this.value4 = value4;
    }

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        return
            "authObject='" + getAuthObject()
                + "', property='" + getAuthObjectProperty()
                + "', value1='" + getValue1()
                + "', value2='" + getValue2()
                + "', value3='" + getValue3()
                + "', value4='" + getValue4() + "'";
    }

}
