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

    private Integer id;
    private AccessConditionType type;
    private AccessPattern pattern;
    private AccessProfileCondition profileCondition;
    private AccessPatternCondition patternCondition;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 7)
    public AccessConditionType getType() {
        return type;
    }

    public void setType(AccessConditionType type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "PatternId")
    public AccessPattern getPattern() {
        return pattern;
    }

    public void setPattern(AccessPattern pattern) {
        this.pattern = pattern;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ProfileConditionId")
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
        this.type = AccessConditionType.ProfileCondition;
        this.patternCondition = null;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "PatternConditionId")
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
        this.type = AccessConditionType.PatternCondition;
        this.profileCondition = null;
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
        return (type == AccessConditionType.ProfileCondition) ? profileCondition.toString() : patternCondition.toString();
    }

}
