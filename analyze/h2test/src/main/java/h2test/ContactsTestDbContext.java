package h2test;

import h2test.entities.Contact;

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

        return list;
    }

}
