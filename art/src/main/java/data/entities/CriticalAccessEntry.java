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
@Table(name = "CriticalAccessQueryEntries")
public class CriticalAccessEntry {

    private Integer id;
    private CriticalAccessQuery query;
    private AccessPattern accessPattern;
    private String username;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "QueryId")
    public CriticalAccessQuery getQuery() {
        return query;
    }

    public void setQuery(CriticalAccessQuery query) {
        this.query = query;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ViolatedPatternId")
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
     * Compares an entry to another entry using userName and useCaseID.
     *
     * @param other the object to compare with
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }

        if (other == this) {
            return true;
        }

        if (!(other instanceof CriticalAccessEntry)) {
            return false;
        }

        CriticalAccessEntry elementToCompare = (CriticalAccessEntry) other;

        return (this.username.equals(elementToCompare.getUsername())
            && this.accessPattern.getUsecaseId().equals(elementToCompare.getAccessPattern().getUsecaseId()));
    }

    @Override
    public int hashCode() {
        return (id != null) ? id : 0;
    }
}
