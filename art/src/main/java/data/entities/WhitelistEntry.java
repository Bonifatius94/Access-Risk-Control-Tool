
package data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This is a data object class for an entry specified in a whitelist.
 * It contains properties like usecase id or username.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "WhitelistEntries")
public class WhitelistEntry {

    public WhitelistEntry() {

    }

    /**
     * This constructor creates a new instance of a whitelist entry.
     *
     * @param usecaseId the usecase id of the whitelist entry
     * @param username the username of the whitelist entry
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public WhitelistEntry(String usecaseId, String username) {

        setUsecaseId(usecaseId);
        setUsername(username);
    }

    private Integer id;
    private String usecaseId;
    private String username;
    private Whitelist whitelist;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    @ManyToOne
    @JoinColumn(name = "WhitelistId")
    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
    }

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        return "usecaseId='" + getUsecaseId() + "', username='" + getUsername() + "'";
    }

    /**
     * This is a custom implementation of equals method that checks for data equality.
     *
     * @param other the object to compare with
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object other) {

        boolean ret = (other == this);

        if (other instanceof WhitelistEntry) {

            WhitelistEntry cmp = (WhitelistEntry) other;

            ret = (this.username.equals(cmp.getUsername())
                && this.usecaseId.equals(cmp.getUsecaseId())
                && ((this.whitelist == null && cmp.getWhitelist() == null) || (this.whitelist != null && this.whitelist.equals(cmp.getWhitelist()))));
        }

        return ret;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id : 0;
    }

}
