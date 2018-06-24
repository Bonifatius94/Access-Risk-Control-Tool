package data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CriticalAccessEntries")
public class CriticalAccessEntry {

    // =============================
    //         constructors
    // =============================

    public CriticalAccessEntry() {
        // nothing to do here ...
    }

    /**
     * This constructor creates a new instance with the given data.
     *
     * @param pattern the pattern of the new instance
     * @param username the username of the new instance
     */
    public CriticalAccessEntry(AccessPattern pattern, String username) {

        setAccessPattern(pattern);
        setUsername(username);
    }

    // =============================
    //            members
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "QueryId")
    private CriticalAccessQuery query;

    @ManyToOne
    @JoinColumn(name = "ViolatedPatternId")
    private AccessPattern accessPattern;

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

    public CriticalAccessQuery getQuery() {
        return query;
    }

    public void setQuery(CriticalAccessQuery query) {
        this.query = query;
    }

    public AccessPattern getAccessPattern() {
        return accessPattern;
    }

    public void setAccessPattern(AccessPattern accessPattern) {
        this.accessPattern = accessPattern;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // =============================
    //          overrides
    // =============================

    @Override
    public String toString() {
        return "ViolatedUseCaseID: " + accessPattern.getUsecaseId() + ", Username: " + username;
    }

}
