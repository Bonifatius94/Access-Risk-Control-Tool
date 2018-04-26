package excel;

public class AuthorizationPatternConditionProperty {

    public AuthorizationPatternConditionProperty(String authObject, String propertyName, String value1, String value2, String value3, String value4) {

        setAuthObject(authObject);
        setPropertyName(propertyName);
        setValue1(value1);
        setValue2(value2);
        setValue3(value3);
        setValue4(value4);
    }

    private String authObject;
    private String propertyName;
    private String value1;
    private String value2;
    private String value3;
    private String value4;

    public String getAuthObject() {
        return authObject;
    }

    public void setAuthObject(String authObject) {
        this.authObject = authObject;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
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

    @Override
    public String toString() {
        return
                "authObject='" + getAuthObject()
                + "', property='" + getPropertyName()
                + "', value1='" + getValue1()
                + "', value2='" + getValue2()
                + "', value3='" + getValue3()
                + "', value4='" + getValue4() + "'";
    }

}
