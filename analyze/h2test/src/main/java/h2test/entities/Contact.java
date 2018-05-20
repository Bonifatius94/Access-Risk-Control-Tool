package h2test.entities;

import h2test.IRecord;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contact")
public class Contact implements IRecord {

    public Contact() {
        // empty constructor, nothing to do here ...
    }

    public Contact(String name, String email) {

        // set members
        setName(name);
        setEmail(email);
    }

    private Integer id;
    private String name;
    private String email;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ID='" + id + "', Name='" + name + "', Email='" + email + "'";
    }

}
