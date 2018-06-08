package data.entities;

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
@Table(name = "AccessProfileConditions")
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

    // =============================
    //          overrides
    // =============================

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

    /**
     * This is a custom implementation of equals method that checks for data equality.
     *
     * @param other the object to compare with
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object other) {

        boolean ret = (other == this);

        if (other instanceof AccessProfileCondition) {

            AccessProfileCondition cmp = (AccessProfileCondition) other;

            ret = profile.equals(cmp.getProfile());
        }

        return ret;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id : 0;
    }

}
