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

    /**
     * Compares an entry to another entry using userName and useCaseID.
     * @param other the object to compare with
     * @return whether they are equal
     */
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof CriticalAccessEntry)) {
            return false;
        }
        return (userName.equals(((CriticalAccessEntry) other).userName) && authorizationPattern.equals(((CriticalAccessEntry) other).getAuthorizationPattern()));
    }
}
