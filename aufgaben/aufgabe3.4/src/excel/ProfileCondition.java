package excel;

public class ProfileCondition implements ICondition {

    public ProfileCondition(String authorizationProfile) {

        setAuthorizationProfile(authorizationProfile);
    }

    private String authorizationProfile;

    public String getAuthorizationProfile() {
        return authorizationProfile;
    }

    public void setAuthorizationProfile(String authorizationProfile) {
        this.authorizationProfile = authorizationProfile;
    }

    @Override
    public String toString() {
        return "profile='" + getAuthorizationProfile() + "'";
    }

}
