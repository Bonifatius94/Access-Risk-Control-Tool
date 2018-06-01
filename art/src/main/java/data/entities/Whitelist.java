package data.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Whitelists")
public class Whitelist {

    private Integer id;
    private String description;

    private boolean isArchived;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WhitelistEntry> entries;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public Set<WhitelistEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<WhitelistEntry> entries) {
        this.entries = entries;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

}
