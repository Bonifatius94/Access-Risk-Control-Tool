package data.entities;

import java.util.HashSet;
import java.util.Set;

public class DbUser {

    // =============================
    //         constructors
    // =============================

    /**
     * This constructor creates a new DbUser with the given username and roles.
     *
     * @param username the username of the new instance
     * @param roles the roles of the new instance
     */
    public DbUser(String username, Set<DbUserRole> roles) {

        setUsername(username);
        this.roles = roles;
    }

    /**
     * This constructor creates a new DbUser with the given username and roles (as boolean flags).
     *
     * @param username the username of the new instance
     * @param isAdmin a flag that indicates whether the new user is admin or not
     * @param isDataAnalyst a flag that indicates whether the new user is data analyst or not
     * @param isViewer a flag that indicates whether the new user is viewer or not
     * @param isFirstLogin a flag that indicates whether the new user is logging in for the first time
     */
    public DbUser(String username, boolean isAdmin, boolean isDataAnalyst, boolean isViewer, boolean isFirstLogin) {

        setUsername(username);
        setFirstLogin(isFirstLogin);

        if (isAdmin) {
            roles.add(DbUserRole.Admin);
        }

        if (isDataAnalyst) {
            roles.add(DbUserRole.DataAnalyst);
        }

        if (isViewer) {
            roles.add(DbUserRole.Viewer);
        }
    }

    // =============================
    //           members
    // =============================

    private String username;
    private Set<DbUserRole> roles = new HashSet<>();
    private boolean isFirstLogin = true;

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

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    // =============================
    //          overrides
    // =============================

    @Override
    public String toString() {
        return "Username = " + getUsername() + ", Roles = " + getRoles().stream().map(x -> x.toString()).reduce((x, y) -> x + ", " + y).get();
    }

}
