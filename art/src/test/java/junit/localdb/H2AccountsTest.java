package junit.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
            context.createDatabaseUser(new DbUser("FooAdmin", new HashSet(Arrays.asList(DbUserRole.Admin, DbUserRole.DataAnalyst))), "foobar");
            context.createDatabaseUser(new DbUser("FooDataAnalyst", new HashSet(Arrays.asList(DbUserRole.Viewer, DbUserRole.DataAnalyst))), "foobar");
            context.createDatabaseUser(new DbUser("FooViewer", new HashSet(Arrays.asList(DbUserRole.Viewer))), "foobar");

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
            String username = "FOO";
            DbUser foo = new DbUser(username, new HashSet(Arrays.asList(DbUserRole.Viewer)));
            context.createDatabaseUser(foo, "foobar");

            // check if an additional user was created
            List<DbUser> users = context.getDatabaseUsers();
            boolean roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals(username)).findFirst().get().getRoles().contains(DbUserRole.Viewer);
            assert(roleOk);

            // apply changes to roles
            foo.getRoles().remove(DbUserRole.Viewer);
            foo.getRoles().add(DbUserRole.DataAnalyst);
            foo.getRoles().add(DbUserRole.Admin);
            context.updateUserRoles(foo);

            // check if the role was changed
            users = context.getDatabaseUsers();
            Set<DbUserRole> roles = users.stream().filter(x -> x.getUsername().toUpperCase().equals(username)).findFirst().get().getRoles();

            ret = roles.containsAll(Arrays.asList(DbUserRole.DataAnalyst));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    @Disabled
    public void testChangePassword() {

        // TODO: implement test
        assert(false);
    }

    @Test
    public void testDeleteDatabaseUser() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            String username = "FOO";
            DbUser foo = new DbUser(username, new HashSet(Arrays.asList(DbUserRole.Viewer)));
            context.createDatabaseUser(foo, "foobar");

            // query new user
            DbUser user = context.getDatabaseUsers().stream().filter(x -> x.getUsername().equals(username)).findFirst().get();

            // test delete role
            context.deleteDatabaseUser("Foo");

            // check if user was deleted
            user = context.getDatabaseUsers().stream().filter(x -> x.getUsername().equals(username)).findFirst().orElse(null);

            ret = (user == null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

}
