package data.local.entities;

import data.local.IRecord;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "contacts")
public class Contact implements IRecord, Serializable {

    // --------------------------------------
    //             Constructors
    // --------------------------------------

    /**
     * This is an empty constructor for a new contact.
     */
    public Contact() {
        // empty constructor, nothing to do here ...
    }

    /**
     * This constructor takes all properties of a contacts and sets them.
     *
     * @param name the name of the new contact
     * @param email the email of the new contact
     * @param company the company of the new contact
     */
    public Contact(String name, String email, Company company) {

        // set members
        setName(name);
        setEmail(email);
        setCompany(company);
    }

    // --------------------------------------
    //              Properties
    // --------------------------------------

    private Integer id;
    private String name;
    private String email;
    private Company company;

    // --------------------------------------
    //             Primary Key
    // --------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // --------------------------------------
    //             Foreign Keys
    // --------------------------------------

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "CompanyId")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // --------------------------------------
    //               Members
    // --------------------------------------

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

    // --------------------------------------
    //             Overrides
    // --------------------------------------

    @Override
    public String toString() {
        return "ID='" + id + "', Company='" + getCompany().getName() + "', Name='" + name + "', Email='" + email + "'";
    }

}
