package data.entities;

import java.util.HashSet;
import java.util.Set;

public class DbUser {

    // =============================
    //         constructors
    // =============================

    /**
     * This constructor creates a new DbUser with the given username and role.
     *
     * @param username the username of the new instance
     * @param roles the roles of the new instance
     */
    public DbUser(String username, Set<DbUserRole> roles) {

        setUsername(username);
        this.roles = roles;
    }

    // =============================
    //           members
    // =============================

    private String username;
    private Set<DbUserRole> roles = new HashSet<>();

    // =============================
    //      getters / setters
    // =============================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<DbUserRole> getRoles() {
        return roles;
    }

    public void addRole(DbUserRole role) {
        roles.add(role);
    }

    public void removeRole(DbUserRole role) {
        roles.remove(role);
    }

    // =============================
    //          overrides
    // =============================

    @Override
    public String toString() {
        return "Username = " + getUsername() + ", Roles = " + getRoles().stream().map(x -> x.toString()).reduce((x, y) -> x + ", " + y).get();
    }

}
