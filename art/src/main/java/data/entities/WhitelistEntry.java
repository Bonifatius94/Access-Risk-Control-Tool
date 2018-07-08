
package data.entities;

import javax.persistence.Column;
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
 */
@Entity
@Table(name = "WhitelistEntries")
public class WhitelistEntry {

    // =============================
    //         constructors
    // =============================

    public WhitelistEntry() {

    }

    /**
     * This constructor creates a new instance of a whitelist entry.
     *
     * @param usecaseId the usecase id of the whitelist entry
     * @param username the username of the whitelist entry
     */
    public WhitelistEntry(String usecaseId, String username) {

        setUsecaseId(usecaseId);
        setUsername(username);
    }

    /**
     * This constructor clones the given instance.
     *
     * @param original the instance to be cloned
     */
    public WhitelistEntry(WhitelistEntry original) {

        this.setUsecaseId(original.getUsecaseId());
        this.setUsername(original.getUsername());
    }

    // =============================
    //           members
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "WhitelistId")
    private Whitelist whitelist;

    @Column(nullable = false)
    private String usecaseId;

    @Column(nullable = false)
    private String username;

    // =============================
    //      getters / setters
    // =============================

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

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
    }

    // =============================
    //          overrides
    // =============================

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     */
    @Override
    public String toString() {
        return "usecaseId='" + getUsecaseId() + "', username='" + getUsername() + "'";
    }

}
