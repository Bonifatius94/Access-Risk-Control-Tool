package sqlite;

import sqlite.entities.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsTestDbContext extends SqliteContextBase {

    public ContactsTestDbContext(String filePath) { super(filePath); }

    @Override
    protected List<Class> getAnnotatedClasses() {

        List<Class> list = new ArrayList<>();
        list.add(Contact.class);

        return list;
    }

}
