package data.entities.analysis;

import data.entities.config.AccessProfileCondition;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

/**
 * This class represents an auth profile condition.
 * It contains properties like condition name or auth profile name and implements the ICondition interface.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
@Entity
@Table(name = "analysis.AccessProfileConditions_History")
public class AccessProfileConditionHistory {

    /**
     * Empty constructor required by hibernate.
     */
    public AccessProfileConditionHistory() {
        // nothing to do here ...
    }

    public AccessProfileConditionHistory(String profile) {

        setProfile(profile);
    }

    /**
     * This constructor converts an access profile condition to an access profile condition history.
     *
     * @param condition the original condition
     */
    public AccessProfileConditionHistory(AccessProfileCondition condition) {

        setProfile(condition.getProfile());
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
