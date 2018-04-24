
package excel;

/**
 * this is a data object class for a user specified in whitelist
 */
@SuppressWarnings("all") // remove this annotation lateron
public class WhitelistUser {

    public WhitelistUser(String userID, String username) {

        setUserID(userID);
        setUsername(username);
    }

    private String UserID;
    private String Username;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    @Override
    public String toString() {
        return "UserID='" + getUserID() + "', Username='" + getUsername() + "'";
    }
}
