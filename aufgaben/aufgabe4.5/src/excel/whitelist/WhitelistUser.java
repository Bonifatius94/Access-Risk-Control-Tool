
package excel.whitelist;

/**
 * this is a data object class for a user specified in whitelist
 */
@SuppressWarnings("all") // remove this annotation lateron
public class WhitelistUser {

    public WhitelistUser(String usecaseID, String username) {

        setUsecaseID(usecaseID);
        setUsername(username);
    }

    private String UsecaseID;
    private String Username;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUsecaseID() {
        return UsecaseID;
    }

    public void setUsecaseID(String usecaseID) {
        UsecaseID = usecaseID;
    }

    @Override
    public String toString() {
        return "UsecaseID='" + getUsecaseID() + "', Username='" + getUsername() + "'";
    }
}
