package data.entities;

public class CriticalAccessEntry {

    private AuthorizationPattern authorizationPattern;
    private String userName;

    public AuthorizationPattern getAuthorizationPattern() {
        return authorizationPattern;
    }

    public void setAuthorizationPattern(AuthorizationPattern authorizationPattern) {
        this.authorizationPattern = authorizationPattern;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
