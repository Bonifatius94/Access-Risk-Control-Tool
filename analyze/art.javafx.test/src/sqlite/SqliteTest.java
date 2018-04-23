package sqlite;

import java.util.List;

import sqlite.entities.Contact;

// hibernate tutorial:
// ===================
// http://www.srccodes.com/p/article/7/Annotation-based-Hibernate-Hello-World-example-using-Maven-build-tool-and-SQLite-database

// encryption options:
// ===================
// custom AES 128 encryption: https://stackoverflow.com/questions/30113937/how-to-encrypt-a-sqlite-database?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
// integrated sqlite encryption: https://stackoverflow.com/questions/5669905/sqlite-with-encryption-password-protection?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

// custom file encryption:
// =======================
// The idea is that the entire SQLite database file is only stored encrypted on the file system.
// Therefore the database is decrypted (by e.g. AES 256 key) on application startup and only stored in-memory.
// When the application closes, the database file is encrypted and stored on the file system.
// For accessing the data, the user needs to log in with his user credentials to verify his identity.
// As the database is encrypted, the user credentials can be simply stored in the SQLite database.

public class SqliteTest {

    public static void main(String[] args) {

        try (ContactsTestDbContext context = new ContactsTestDbContext("Mydb.db")) {

            Contact myContact = new Contact(3, "My Name", "my_email@email.com");
            Contact yourContact = new Contact(24, "Your Name", "your_email@email.com");
            context.insertRecord(myContact);
            context.insertRecord(yourContact);

            List<Contact> contactList = context.queryDataset("FROM Contact");

            for (Contact contact : contactList) {
                System.out.println(contact);
            }

            context.deleteRecord(myContact);
            context.deleteRecord(yourContact);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}