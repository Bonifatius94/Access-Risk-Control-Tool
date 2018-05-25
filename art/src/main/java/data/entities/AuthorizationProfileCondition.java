//package data.entities;
//
///**
// * This class represents an auth profile condition.
// * It contains properties like condition name or auth profile name and implements the ICondition interface.
// *
// * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
// */
//public class AuthorizationProfileCondition implements ICondition {
//
//    /**
//     * This constructor creates a new instance of auth profile condition.
//     *
//     * @param authorizationProfile the auth profile name set to this new instance.
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public AuthorizationProfileCondition(String authorizationProfile) {
//
//        setAuthorizationProfile(authorizationProfile);
//    }
//
//    private String conditionName = "<name missing>";
//    private String authorizationProfile;
//
//    /**
//     * This method gets the auth profile name of this instance.
//     *
//     * @return the auth profile name of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public String getAuthorizationProfile() {
//        return authorizationProfile;
//    }
//
//    /**
//     * This method sets the auth profile name of this instance.
//     *
//     * @param authorizationProfile the new auth profile name of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    public void setAuthorizationProfile(String authorizationProfile) {
//        this.authorizationProfile = authorizationProfile;
//    }
//
//    /**
//     * This method gets the custom condition name of this instance (implements the method specified in ICondition).
//     *
//     * @return the custom condition name of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    @Override
//    public String getConditionName() {
//        return conditionName;
//    }
//
//    /**
//     * This method sets the custom condition name of this instance (implements the method specified in ICondition).
//     *
//     * @param conditionName new custom condition name of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    @Override
//    public void setConditionName(String conditionName) {
//        this.conditionName = conditionName;
//    }
//
//    /**
//     * This method gets the condition type of this instance (implements the method specified in ICondition).
//     *
//     * @return the condition type of this instance
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    @Override
//    public String getConditionType() {
//        return "Profile Pattern";
//    }
//
//    /**
//     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
//     *
//     * @return JSON-like data representation of this instance as a string
//     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
//     */
//    @Override
//    public String toString() {
//        return "profile='" + getAuthorizationProfile() + "'";
//    }
//
//}
