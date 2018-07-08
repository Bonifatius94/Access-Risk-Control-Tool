package junit.localdb;

import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            context.createDatabaseUser(new DbUser("FooAdmin", new HashSet<>(Arrays.asList(DbUserRole.Admin, DbUserRole.Configurator))), "foobar");
            context.createDatabaseUser(new DbUser("FooConfigurator", new HashSet<>(Arrays.asList(DbUserRole.Viewer, DbUserRole.Configurator))), "foobar");
            context.createDatabaseUser(new DbUser("FooViewer", new HashSet<>(Arrays.asList(DbUserRole.Viewer))), "foobar");

            // query accounts
            List<DbUser> users = context.getDatabaseUsers();
            ret = users.size() == 6;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testChangeUserRole() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            String username = "FOO";
            DbUser foo = new DbUser(username, new HashSet<>(Arrays.asList(DbUserRole.Viewer)));
            context.createDatabaseUser(foo, "foobar");

            // check if an additional user was created
            List<DbUser> users = context.getDatabaseUsers();
            boolean roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals(username)).findFirst().get().getRoles().contains(DbUserRole.Viewer);
            assert (roleOk);

            // apply changes to roles
            foo.getRoles().remove(DbUserRole.Viewer);
            foo.getRoles().add(DbUserRole.Configurator);
            foo.getRoles().add(DbUserRole.Admin);
            context.updateUserRoles(foo);

            // check if the role was changed
            users = context.getDatabaseUsers();
            Set<DbUserRole> roles = users.stream().filter(x -> x.getUsername().toUpperCase().equals(username)).findFirst().get().getRoles();

            ret = roles.containsAll(Arrays.asList(DbUserRole.Configurator));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testChangePassword() {

        boolean ret = false;

        final String username = "FOO";
        final String oldPassword = "foobar";
        final String newPassword = "raboof";

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            DbUser foo = new DbUser(username, new HashSet<>(Arrays.asList(DbUserRole.Viewer)));
            context.createDatabaseUser(foo, oldPassword);

            // check if an additional user was created
            List<DbUser> users = context.getDatabaseUsers();
            boolean roleOk = users.stream().filter(x -> x.getUsername().toUpperCase().equals(username)).findFirst().get().getRoles().contains(DbUserRole.Viewer);
            assert (roleOk);

            // set new password
            context.changePassword(username, newPassword);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // try to login with old password (should fail)
        try (ArtDbContext context = new ArtDbContext(username, oldPassword)) {

            // login with old password must not be successful
            assert (false);

        } catch (Exception ex) {
            // nothing to do here ...
        }

        // try to login with old password (should work)
        try (ArtDbContext context = new ArtDbContext(username, newPassword)) {

            // login with new password successful
            ret = true;

        } catch (Exception ex) {
            // nothing to do here ...
        }

        assert (ret);
    }

    @Test
    public void testDeleteDatabaseUser() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            String username = "FOO";
            DbUser foo = new DbUser(username, new HashSet<>(Arrays.asList(DbUserRole.Viewer)));
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

        assert (ret);
    }

    @Test
    public void testFirstLogin() {

        boolean ret = false;

        final String username = "testuser";
        final String password = "vabsojgbojrwiotioewhr";

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create new user as viewer
            DbUser user = new DbUser(username, new HashSet<>(Arrays.asList(DbUserRole.Viewer)));
            context.createDatabaseUser(user, password);

            // check if first login role was assigned
            ret = context.getDatabaseUsers().stream().anyMatch(x -> x.getUsername().equals(username.toUpperCase()) && x.isFirstLogin());
            assert (ret);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (ArtDbContext context = new ArtDbContext(username, password)) {

            // check if it is the first login (which should be true)
            DbUser user = context.getCurrentUser();
            ret = ret && user.isFirstLogin();

            // set first login
            context.setFirstLoginOfCurrentUser(user, false);

            // check if first login role was removed by isFirstLogin()
            ret = ret && !user.isFirstLogin() && !context.getCurrentUser().isFirstLogin();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

}


