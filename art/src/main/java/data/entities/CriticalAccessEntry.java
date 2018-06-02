package data.entities;

import javax.persistence.CascadeType;
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

    private Integer id;
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

    public String toString() {
        return "ViolatedUseCaseID: " + accessPattern.getUsecaseId() + ", Username: " + username;
    }

    /**
     * Compares an entry to another entry using userName and useCaseID.
     * @param other the object to compare with
     * @return whether they are equal
     */
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
        return (username.equals(((CriticalAccessEntry) other).username) && accessPattern.getUsecaseId().equals(((CriticalAccessEntry) other).getAccessPattern().getUsecaseId()));
    }
}
