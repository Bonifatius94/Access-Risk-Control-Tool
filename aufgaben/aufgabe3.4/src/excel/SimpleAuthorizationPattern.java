package excel;

public class SimpleAuthorizationPattern extends AuthorizationPattern {

    public SimpleAuthorizationPattern(ICondition condition) {

        setCondition(condition);
    }

    public SimpleAuthorizationPattern(String name, String description, ICondition condition) {

        super(name, description);
        setCondition(condition);
    }

    private ICondition condition;

    public ICondition getCondition() {
        return condition;
    }

    public void setCondition(ICondition condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return super.toString() + ", condition: " + condition.toString();
    }

}
