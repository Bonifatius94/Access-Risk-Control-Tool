package data.entities.analysis;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "analysis.CriticalAccessEntries")
public class CriticalAccessEntry {

    private AccessPatternHistory accessPattern;
    private String username;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ViolatedPatternId")
    public AccessPatternHistory getAccessPattern() {
        return accessPattern;
    }

    public void setAccessPattern(AccessPatternHistory accessPattern) {
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
}
