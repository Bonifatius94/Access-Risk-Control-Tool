package data.entities;

public class DbUser {

    /**
     * This constructor creates a new DbUser with the given username and role.
     *
     * @param username the username of the new instance
     * @param role the role of the new instance
     */
    public DbUser(String username, DbUserRole role) {

        setUsername(username);
        setRole(role);
    }

    private String username;
    private DbUserRole role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DbUserRole getRole() {
        return role;
    }

    public void setRole(DbUserRole role) {
        this.role = role;
    }

    // =============================
    //          overrides
    // =============================

    @Override
    public String toString() {
        return "Username = " + getUsername() + ", Role = " + getRole().toString();
    }

}
