package excel;

public abstract class AuthorizationPattern {

    public AuthorizationPattern() { }

    public AuthorizationPattern(String usecaseID, String description) {

        setUsecaseID(usecaseID);
        setDescription(description);
    }

    private String usecaseID;
    private String description;

    public String getUsecaseID() {
        return usecaseID;
    }

    public void setUsecaseID(String usecaseID) {
        this.usecaseID = usecaseID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "usecaseID='" + getUsecaseID() + "', description='" + getDescription() + "'";
    }

}
