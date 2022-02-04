package data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This class represents an auth profile condition.
 * It contains properties like condition name or auth profile name and implements the ICondition interface.
 */
@Entity
@Table(name = "AccessProfileConditions")
public class AccessProfileCondition {

    // =============================
    //         constructors
    // =============================

    public AccessProfileCondition() {
        // nothing to do here ...
    }

    public AccessProfileCondition(String profile) {

        setProfile(profile);
    }

    /**
     * This constructor clones the given instance.
     *
     * @param original the instance to be cloned
     */
    public AccessProfileCondition(AccessProfileCondition original) {
        this.setProfile(original.getProfile());
    }

    // =============================
    //           members
    // =============================

    @Id
    private Integer id;

    @OneToOne
    @MapsId
    private AccessCondition condition;

    @Column(nullable = false)
    private String profile;

    // =============================
    //      getters / setters
    // =============================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccessCondition getCondition() {
        return condition;
    }

    public void setCondition(AccessCondition condition) {
        this.condition = condition;
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
     */
    @Override
    public String toString() {
        return "profile='" + getProfile() + "'";
    }

}
