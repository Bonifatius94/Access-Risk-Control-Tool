
package data.entities;

import javax.persistence.CascadeType;
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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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
}
