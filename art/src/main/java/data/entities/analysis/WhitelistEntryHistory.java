
package data.entities.analysis;

import data.entities.config.WhitelistEntry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

/**
 * This is a data object class for an entry specified in a whitelist.
 * It contains properties like usecase id or username.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "analysis.WhitelistEntries_History")
public class WhitelistEntryHistory {

    /**
     * Empty constructor required by hibernate.
     */
    public WhitelistEntryHistory() {
        // nothing to do here ...
    }

    /**
     * This constructor creates a new instance of a whitelist entry.
     *
     * @param usecaseId the usecase id of the whitelist entry
     * @param username the username of the whitelist entry
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public WhitelistEntryHistory(String usecaseId, String username) {

        setUsecaseId(usecaseId);
        setUsername(username);
    }

    /**
     * This constructor converts a whitelist entry to a whitelist entry history.
     *
     * @param entry the original whitelist entry
     */
    public WhitelistEntryHistory(WhitelistEntry entry) {

        setUsecaseId(entry.getUsecaseId());
        setUsername(entry.getUsername());
    }

    private Integer id;
    private String usecaseId;
    private String username;
    private WhitelistHistory whitelist;

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
    public WhitelistHistory getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(WhitelistHistory whitelist) {
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
