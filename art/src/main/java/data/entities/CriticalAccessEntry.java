package data.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CriticalAccessEntries")
public class CriticalAccessEntry {

    public CriticalAccessEntry() {

    }

    public CriticalAccessEntry(AccessPattern pattern, String username) {
        setAccessPattern(pattern);
        setUsername(username);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "QueryId")
    private CriticalAccessQuery query;

    @ManyToOne
    @JoinColumn(name = "ViolatedPatternId")
    private AccessPattern accessPattern;

    private String username;

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

    /**
     * This is a custom implementation of equals method that checks for data equality.
     *
     * @param other the object to compare with
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object other) {

        /*boolean ret = (other == this);

        if (other instanceof CriticalAccessEntry) {

            CriticalAccessEntry cmp = (CriticalAccessEntry) other;

            ret = (this.username.equals(cmp.getUsername())
                && ((this.query == null && cmp.getQuery() == null) || (this.query != null && this.query.equals(cmp.getQuery())))
                && ((this.accessPattern == null && cmp.getAccessPattern() == null) || (this.accessPattern != null && this.accessPattern.equals(cmp.getAccessPattern()))));
        }

        return ret;*/

        return super.equals(other);
    }

    @Override
    public int hashCode() {
        //return (id != null) ? id : 0;
        return super.hashCode();
    }
}
