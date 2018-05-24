package data.localdb;

import data.entities.Company;
import data.entities.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsTestDbContext extends H2ContextBase {

    public ContactsTestDbContext(String username, String password) {
        super(username, password);
    }

    @Override
    protected List<Class> getAnnotatedClasses() {

        List<Class> list = new ArrayList<>();
        list.add(Contact.class);
        list.add(Company.class);

        return list;
    }

}
