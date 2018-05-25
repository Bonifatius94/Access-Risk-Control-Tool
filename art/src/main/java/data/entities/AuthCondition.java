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
public class AuthCondition {

    private Integer id;
    private AuthConditionType type;
    private AuthProfileCondition profileCondition;
    private AuthPatternCondition patternCondition;

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
    public AuthConditionType getType() {
        return type;
    }

    public void setType(AuthConditionType type) {
        this.type = type;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ProfileConditionId")
    public AuthProfileCondition getProfileCondition() {
        return profileCondition;
    }

    public void setProfileCondition(AuthProfileCondition profileCondition) {
        this.profileCondition = profileCondition;
        this.type = AuthConditionType.ProfileCondition;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "PatternConditionId")
    public AuthPatternCondition getPatternCondition() {
        return patternCondition;
    }

    public void setPatternCondition(AuthPatternCondition patternCondition) {
        this.patternCondition = patternCondition;
        this.type = AuthConditionType.PatternCondition;
    }

}
