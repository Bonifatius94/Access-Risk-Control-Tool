package junit.localdb;

import data.entities.Whitelist;
import data.localdb.ArtDbContext;

import io.msoffice.excel.WhitelistImportHelper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WhitelistTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {

        System.out.print("cleaning up datebase ...");
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");
        System.out.println(" done");

        String currentExeFolder = System.getProperty("user.dir");
        Path scriptPath = Paths.get(currentExeFolder, "target", "test-classes", "scripts", "create_test_data_seed.sql");
        List<String> sqlStatements = Files.readAllLines(scriptPath);

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            try (Session session = context.openSession()) {

                Transaction transaction = session.beginTransaction();
                sqlStatements.forEach(sql -> session.createNativeQuery(sql).executeUpdate());
                transaction.commit();
            }
        }

        System.out.println("created a new test database and inserted test data");
    }

    private static void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

    @Test
    public void testQueryWhitelist() throws Exception {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists
            List<Whitelist> whitelists = context.getWhitelists();

            // check if test data was queried successfully
            ret = whitelists.size() == 1 && whitelists.stream().allMatch(x -> x.getEntries().size() == 7);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("whitelist query test " + (ret ? "successful" : "failed"));
        assertTrue(ret);
    }

    @Test
    public void testCreateWhitelist() throws Exception {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists before insertion
            List<Whitelist> whitelists = context.getWhitelists();
            int countBefore = (whitelists != null) ? whitelists.size() : 0;

            // insert a whitelist
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            whitelist.setDescription("a test description");
            System.out.println(whitelist);
            context.createWhitelist(whitelist);

            // query whitelists after insertion
            whitelists = context.getWhitelists();
            System.out.println(whitelist);
            int countAfter = (whitelists != null) ? whitelists.size() : 0;

            // compare counts
            ret = (countBefore + 1 == countAfter);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("whitelist creation test " + (ret ? "successful" : "failed"));
        assert(ret);
    }

    /*@Test
    public void testUpdateWhitelist() throws Exception {

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create a new whitelist
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            System.out.println(whitelist);
            context.createWhitelist(whitelist);

            // update

        } catch (Exception ex) {

            System.out.println("whitelist update test failed");
            ex.printStackTrace();
        }
    }*/

    /*@Test
    public void testDeleteWhitelist() {

        boolean ret = false;

        // delete database to ensure a clean test environment
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            try (Session session = context.openSession()) {

                Transaction transaction = session.beginTransaction();

                // insert an unarchived whitelists
                int rowsAffected = session.createNativeQuery(
                    "INSERT INTO Whitelists (archived, createdAt, createdBy, description, id) "
                        + "VALUES (0, '2018-06-08T15:09:15', 'test', 'test description', 1)"
                ).executeUpdate();

                rowsAffected = session.createNativeQuery(
                    "INSERT INTO WhitelistEntries (usecaseId, username, WhitelistId, id) "
                        + "VALUES ('usecase 1', 'test123', 1, 1)"
                ).executeUpdate();

                // insert an archived whitelists
                rowsAffected = session.createNativeQuery(
                    "INSERT INTO Whitelists (archived, createdAt, createdBy, description, id) "
                        + "VALUES (1, '2018-06-08T15:09:15', 'test', 'test description', 2)"
                ).executeUpdate();

                rowsAffected = session.createNativeQuery(
                    "INSERT INTO WhitelistEntries (usecaseId, username, WhitelistId, id) "
                        + "VALUES ('usecase 1', 'test123', 1, 2)"
                ).executeUpdate();

                transaction.commit();
            }

            // query whitelists
            List<Whitelist> whitelists = context.getWhitelists();

            // check if test data was queried successfully
            ret = whitelists != null && whitelists.size() == 2 && whitelists.stream().allMatch(x -> x.getEntries().size() == 3);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

}
