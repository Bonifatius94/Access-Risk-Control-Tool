package data.local;

import data.local.entities.Company;
import data.local.entities.Contact;

import java.util.List;

public class H2Test {

    public static void main(String[] args) {

        // create db context
        try (ContactsTestDbContext context = new ContactsTestDbContext("", "")) {

            // create new records and insert them into database
            Company company = new Company("Contoso Inc.");
            Contact contact1 = new Contact("Max Mustermann", "max.mustermann@email.com", company);
            Contact contact2 = new Contact("John Doe", "john.doe@email.com", company);
            context.insertRecord(company);
            context.insertRecord(contact1);
            context.insertRecord(contact2);

            // query recently inserted records and print them to console
            List<Contact> contactList = context.queryDataset("FROM Contact");
            contactList.forEach(System.out::println);

            // modify records
            contact1.setName("Anna Mustermann");
            contact1.setEmail("anna.mustermann@email.com");
            contact2.setName("Jane Doe");
            contact2.setEmail("jane.doe@email.com");
            context.updateRecord(contact1);
            context.updateRecord(contact2);

            // query recently updated records and print them to console
            contactList = context.queryDataset("FROM Contact");
            contactList.forEach(System.out::println);

            // delete records (clean-up database)
            context.deleteRecord(contact1);
            context.deleteRecord(contact2);
            context.deleteRecord(company);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}