package excel.config;

public class AuthorizationProfileCondition implements ICondition {

    public AuthorizationProfileCondition(String authorizationProfile) {

        setAuthorizationProfile(authorizationProfile);
    }

    private String conditionName;
    private String authorizationProfile;

    public String getAuthorizationProfile() {
        return authorizationProfile;
    }

    public void setAuthorizationProfile(String authorizationProfile) {
        this.authorizationProfile = authorizationProfile;
    }

    @Override
    public String getConditionName() {
        return conditionName;
    }

    @Override
    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    @Override
    public String getConditionType() {
        return "Profile Pattern";
    }

    @Override
    public String toString() {
        return "profile='" + getAuthorizationProfile() + "'";
    }

}
