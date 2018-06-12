package data.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AccessConditions")
public class AccessCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 7)
    private AccessConditionType type;

    @ManyToOne
    @JoinColumn(name = "PatternId")
    private AccessPattern pattern;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ProfileConditionId")
    private AccessProfileCondition profileCondition;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PatternConditionId")
    private AccessPatternCondition patternCondition;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccessConditionType getType() {
        return type;
    }

    public void setType(AccessConditionType type) {
        this.type = type;
    }


    public AccessPattern getPattern() {
        return pattern;
    }

    public void setPattern(AccessPattern pattern) {
        this.pattern = pattern;
    }

    public AccessProfileCondition getProfileCondition() {
        return profileCondition;
    }

    /**
     * This method applies a profile condition to this instance.
     * It also makes sure that no pattern condition is applied and the type is set correctly.
     *
     * @param profileCondition the profile condition to be applied
     */
    public void setProfileCondition(AccessProfileCondition profileCondition) {

        this.profileCondition = profileCondition;
        this.type = AccessConditionType.Profile;
        this.patternCondition = null;
    }

    public AccessPatternCondition getPatternCondition() {
        return patternCondition;
    }

    /**
     * This method applies a pattern condition to this instance.
     * It also makes sure that no profile condition is applied and the type is set correctly.
     *
     * @param patternCondition the pattern condition to be applied
     */
    public void setPatternCondition(AccessPatternCondition patternCondition) {

        this.patternCondition = patternCondition;
        this.type = AccessConditionType.Pattern;
        this.profileCondition = null;
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
        return (type == AccessConditionType.Profile) ? profileCondition.toString() : patternCondition.toString();
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

        if (other instanceof AccessCondition) {

            AccessCondition cmp = (AccessCondition) other;

            ret = (type == cmp.getType()
                && ((this.pattern == null && cmp.getPattern() == null) || (this.pattern != null && this.pattern.equals(cmp.getPattern())))
                && ((this.profileCondition == null && cmp.getProfileCondition() == null) || (this.profileCondition != null && this.profileCondition.equals(cmp.getProfileCondition())))
                && ((this.patternCondition == null && cmp.getPatternCondition() == null) || (this.patternCondition != null && this.patternCondition.equals(cmp.getPatternCondition()))));
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
