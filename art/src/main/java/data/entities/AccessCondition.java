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

    public AccessCondition() {

    }

    public AccessCondition(AccessPattern pattern, AccessPatternCondition patternCondition) {
        setPattern(pattern);
        setPatternCondition(patternCondition);
    }

    public AccessCondition(AccessPattern pattern, AccessProfileCondition profileCondition) {
        setPattern(pattern);
        setProfileCondition(profileCondition);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 7, nullable = false)
    private AccessConditionType type;

    @ManyToOne
    @JoinColumn(name = "PatternId")
    private AccessPattern pattern;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "condition", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "ProfileConditionId")
    private AccessProfileCondition profileCondition;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "condition", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "PatternConditionId")
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
        this.profileCondition.setCondition(this);
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
        this.patternCondition.setCondition(this);
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
