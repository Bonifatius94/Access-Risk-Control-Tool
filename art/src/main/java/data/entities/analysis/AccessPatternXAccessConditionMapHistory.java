package data.entities.analysis;

import data.entities.config.AccessPatternXAccessConditionMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// TODO: check if inheriting from the base class can make most of this implementation obsolete

@Entity
@Table(name = "analysis.nm_AccessPattern_AccessCondition_History")
public class AccessPatternXAccessConditionMapHistory {

    /**
     * Empty constructor required by hibernate.
     */
    public AccessPatternXAccessConditionMapHistory() {
        // nothing to do here ...
    }

    public AccessPatternXAccessConditionMapHistory(AccessPatternXAccessConditionMap map) {

        // TODO: implement logic
    }

    private Integer id;
    private AccessPatternHistory pattern;
    private AccessPatternConditionHistory condition;

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
    public AccessPatternHistory getPattern() {
        return pattern;
    }

    public void setPattern(AccessPatternHistory pattern) {
        this.pattern = pattern;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ConditionId")
    public AccessPatternConditionHistory getCondition() {
        return condition;
    }

    public void setCondition(AccessPatternConditionHistory condition) {
        this.condition = condition;
    }

}
