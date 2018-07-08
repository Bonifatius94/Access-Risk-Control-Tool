package data.entities;

public enum AccessConditionType {

    Profile, Pattern;

    @Override
    public String toString() {
        switch (this) {
            case Pattern: return "Pattern";
            case Profile: return "Profile";
            default: throw new IllegalArgumentException("Cannot convert enum to string. Unknown type.");
        }
    }

}
