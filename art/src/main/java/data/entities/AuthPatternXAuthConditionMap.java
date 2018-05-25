package data.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "art.nm_AuthPattern_AuthCondition")
public class AuthPatternXAuthConditionMap {

    private Integer id;
    private AuthPattern pattern;
    private AuthPatternCondition condition;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "AuthPatternId")
    public AuthPattern getPattern() {
        return pattern;
    }

    public void setPattern(AuthPattern pattern) {
        this.pattern = pattern;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ConditionId")
    public AuthPatternCondition getCondition() {
        return condition;
    }

    public void setCondition(AuthPatternCondition condition) {
        this.condition = condition;
    }

}
