package data.entities;

public class CriticalAccessEntry {

    private AccessPattern authorizationPattern;
    private String userName;

    public AccessPattern getAuthorizationPattern() {
        return authorizationPattern;
    }

    public void setAuthorizationPattern(AccessPattern authorizationPattern) {
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
