package data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ArtUsers")
public class DbUser {

    private String username;
    private DbUserRole role;

    @Id
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 11)
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
