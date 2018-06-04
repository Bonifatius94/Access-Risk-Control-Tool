package data.entities;

public enum DbUserRole {

    // define the three user roles
    Admin, DataAnalyst, Viewer;

    /**
     * This is an implementation of toString for enum synchronization with local database roles.
     *
     * @return a representation of the user role as String
     */
    @Override
    public String toString() {

        switch (this) {
            case Admin: return "Admin";
            case DataAnalyst: return "DataAnalyst";
            case Viewer: return "Viewer";
            default: throw new IllegalArgumentException("Unknown user role");
        }
    }
}
