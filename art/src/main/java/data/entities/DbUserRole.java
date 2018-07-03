package data.entities;

public enum DbUserRole {

    // define the three user roles
    Admin, Configurator, Viewer;

    /**
     * This is an implementation of toString for enum synchronization with local database roles.
     *
     * @return a representation of the user role as String
     */
    @Override
    public String toString() {

        switch (this) {
            case Admin: return "Admin";
            case Configurator: return "Configurator";
            case Viewer: return "Viewer";
            default: throw new IllegalArgumentException("Unknown user role");
        }
    }

    /**
     * This method converts the given String to a user role type (if it fits).
     *
     * @param value the user role as String
     * @return the parsed user role type
     */
    public static DbUserRole parseRole(String value) {

        switch (value.toUpperCase()) {
            case "ADMIN": return Admin;
            case "CONFIGURATOR": return Configurator;
            case "VIEWER": return Viewer;
            default: throw new IllegalArgumentException("Unknown user role");
        }
    }
}
