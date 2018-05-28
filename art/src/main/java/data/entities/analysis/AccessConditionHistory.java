package data.entities.analysis;

import data.entities.AccessConditionType;
import data.entities.config.AccessCondition;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

@Entity
@Table(name = "analysis.AccessConditions_History")
public class AccessConditionHistory {

    /**
     * Empty constructor required by hibernate.
     */
    public AccessConditionHistory() {

    }

    /**
     * This constructor converts an access condition to an access condition history.
     *
     * @param condition the original condition
     */
    public AccessConditionHistory(AccessCondition condition) {

        setType(condition.getType());
        setProfileCondition((condition.getProfileCondition() != null) ? new AccessProfileConditionHistory(condition.getProfileCondition()) : null);
        setPatternCondition((condition.getPatternCondition() != null) ? new AccessPatternConditionHistory(condition.getPatternCondition()) : null);
    }

    private Integer id;
    private AccessConditionType type;
    private AccessProfileConditionHistory profileCondition;
    private AccessPatternConditionHistory patternCondition;

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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ProfileConditionId")
    public AccessProfileConditionHistory getProfileCondition() {
        return profileCondition;
    }

    public void setProfileCondition(AccessProfileConditionHistory profileCondition) {
        this.profileCondition = profileCondition;
        this.type = AccessConditionType.ProfileCondition;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "PatternConditionId")
    public AccessPatternConditionHistory getPatternCondition() {
        return patternCondition;
    }

    public void setPatternCondition(AccessPatternConditionHistory patternCondition) {
        this.patternCondition = patternCondition;
        this.type = AccessConditionType.PatternCondition;
    }

}
