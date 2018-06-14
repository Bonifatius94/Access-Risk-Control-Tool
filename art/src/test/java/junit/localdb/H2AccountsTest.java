package junit.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("all")
public class H2AccountsTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testCreateDatabaseUser() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new database accounts
            context.createDatabaseUser("FooAdmin", "foobar", DbUserRole.Admin);
            context.createDatabaseUser("FooDataAnalyst", "foobar", DbUserRole.DataAnalyst);
            context.createDatabaseUser("FooViewer", "foobar", DbUserRole.Viewer);

            // query accounts
            List<DbUser> users = context.getDatabaseUsers();
            ret = users.size() == 6;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testChangeUserRole() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            context.createDatabaseUser("Foo", "foobar", DbUserRole.Viewer);

            // check if an additional user was created
            List<DbUser> users = context.getDatabaseUsers();
            boolean roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals("FOO")).findFirst().get().getRole() == DbUserRole.Viewer;
            assert(roleOk);

            // change role to data analyst
            context.changeUserRole("Foo", DbUserRole.DataAnalyst);

            // check if the role was changed
            users = context.getDatabaseUsers();
            roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals("FOO")).findFirst().get().getRole() == DbUserRole.DataAnalyst;

            ret = roleOk;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testChangePassword() {

        // TODO: implement test

        assert(false);
    }

    @Test
    public void testDeleteDatabaseUser() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            context.createDatabaseUser("Foo", "foobar", DbUserRole.Viewer);

            // query new user
            DbUser user = context.getDatabaseUsers().stream().filter(x -> x.getUsername().equals("FOO")).findFirst().get();

            // test delete role
            context.deleteDatabaseUser("Foo");

            // check if user was deleted
            user = context.getDatabaseUsers().stream().filter(x -> x.getUsername().equals("FOO")).findFirst().orElse(null);

            ret = user == null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

}
