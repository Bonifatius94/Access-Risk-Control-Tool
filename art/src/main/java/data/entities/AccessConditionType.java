package data.entities;

public enum AccessConditionType {

    ProfileCondition, PatternCondition;

    @Override
    public String toString() {
        switch (this) {
            case PatternCondition: return "Pattern";
            case ProfileCondition: return "Profile";
            default: throw new IllegalArgumentException("Cannot convert enum to string. Unknown type.");
        }
    }

}
