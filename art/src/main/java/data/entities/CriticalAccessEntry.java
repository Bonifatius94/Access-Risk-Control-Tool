package data.entities;

public class CriticalAccessEntry {

    private AuthPattern authorizationPattern;
    private String userName;

    public AuthPattern getAuthorizationPattern() {
        return authorizationPattern;
    }

    public void setAuthorizationPattern(AuthPattern authorizationPattern) {
        this.authorizationPattern = authorizationPattern;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString() {
        return "ViolatedUseCaseID: " + authorizationPattern.getUsecaseId() + ", UserName: " + userName;
    }
}
