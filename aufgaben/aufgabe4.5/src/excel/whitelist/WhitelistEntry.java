
package excel.whitelist;

/**
 * This is a data object class for an entry specified in a whitelist.
 * It contains properties like usecase id or username.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class WhitelistEntry {

    /**
     * This constructor creates a new instance of a whitelist entry.
     *
     * @param usecaseID the usecase id of the whitelist entry
     * @param username the username of the whitelist entry
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public WhitelistEntry(String usecaseID, String username) {

        setUsecaseID(usecaseID);
        setUsername(username);
    }

    private String UsecaseID;
    private String Username;

    /**
     * This method gets the username of this instance.
     *
     * @return username of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getUsername() {
        return Username;
    }

    /**
     * This method sets the username of this instance.
     *
     * @param username new username of this instance.
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setUsername(String username) {
        Username = username;
    }

    /**
     * This method gets the usecase id of this instance.
     *
     * @return usecase id of this instance
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getUsecaseID() {
        return UsecaseID;
    }

    /**
     * This method sets the usecase id of this instance.
     *
     * @param usecaseID new usecase id of this instance.
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setUsecaseID(String usecaseID) {
        UsecaseID = usecaseID;
    }

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        return "UsecaseID='" + getUsecaseID() + "', Username='" + getUsername() + "'";
    }
}
