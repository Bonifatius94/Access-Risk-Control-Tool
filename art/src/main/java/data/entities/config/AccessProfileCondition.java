package data.entities.config;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class represents an auth profile condition.
 * It contains properties like condition name or auth profile name and implements the ICondition interface.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "config.AccessProfileConditions")
public class AccessProfileCondition {

    public AccessProfileCondition() {
        // nothing to do here ...
    }

    public AccessProfileCondition(String profile) {

        setProfile(profile);
    }

    private Integer id;
    private String profile;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * This is a new implementation of toString method for writing this instance to console in JSON-like style.
     *
     * @return JSON-like data representation of this instance as a string
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @Override
    public String toString() {
        return "profile='" + getProfile() + "'";
    }

}
