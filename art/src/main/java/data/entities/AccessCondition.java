package data.entities;

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

@Entity
@Table(name = "art.AuthConditions")
public class AccessCondition {

    private Integer id;
    private AccessConditionType type;
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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ProfileConditionId")
    public AccessProfileCondition getProfileCondition() {
        return profileCondition;
    }

    public void setProfileCondition(AccessProfileCondition profileCondition) {
        this.profileCondition = profileCondition;
        this.type = AccessConditionType.ProfileCondition;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "PatternConditionId")
    public AccessPatternCondition getPatternCondition() {
        return patternCondition;
    }

    public void setPatternCondition(AccessPatternCondition patternCondition) {
        this.patternCondition = patternCondition;
        this.type = AccessConditionType.PatternCondition;
    }

}
