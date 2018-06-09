package junit.localdb;

import data.entities.Whitelist;
import data.localdb.ArtDbContext;

import io.msoffice.excel.WhitelistImportHelper;

import java.io.File;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

public class WhitelistTest {

    private static void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

    // REMARK: The query whitelist operation cannot be tested properly because it depends on the create whitelist operation.
    // So we assume that the standard query (SELECT * FROM Whitelists) is working correctly.

    @Test
    public void testQueryWhitelist() throws Exception {

        // delete database to ensure a clean test environment
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            try (Session session = context.openSession()) {

                Transaction transaction = session.beginTransaction();

                int rowsAffected = session.createNativeQuery(
                    "INSERT INTO Whitelists (archived, createdAt, createdBy, description, id) "
                    + "VALUES (0, '2018-06-08T15:09:15', 'test', )"
                ).executeUpdate();

                //insert into Whitelists (archived, createdAt, createdBy, description, id) values (?, ?, ?, ?, ?)
                //insert into WhitelistEntries (usecaseId, username, WhitelistId, id) values (?, ?, ?, ?)

                Whitelist whitelist = new Whitelist();
                whitelist.setDescription("This is a test description. \r\n1234");
            }

        } catch (Exception ex) {

            System.out.println("whitelist creation test failed");
            ex.printStackTrace();
        }
    }

    @Test
    public void testCreateWhitelist() throws Exception {

        // delete database to ensure a clean test environment
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists before insertion
            List<Whitelist> whitelists = context.getWhitelists();
            int countBefore = (whitelists != null) ? whitelists.size() : 0;
            System.out.println("whitelists count before insertion: " + countBefore);
            System.out.println();

            // insert another whitelist
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            System.out.println(whitelist);
            context.createWhitelist(whitelist);

            // query whitelists after insertion
            whitelists = context.getWhitelists();
            int countAfter = (whitelists != null) ? whitelists.size() : 0;
            System.out.println("whitelists count after insertion: " + countAfter);
            System.out.println();
            System.out.println(whitelists.get(0));

            // compare counts
            System.out.println("whitelist creation test " + (countBefore + 1 == countAfter ? "successful" : "failed"));

        } catch (Exception ex) {

            System.out.println("whitelist creation test failed");
            ex.printStackTrace();
        }
    }

    @Test
    public void testUpdateWhitelist() throws Exception {

        // delete database to ensure a clean test environment
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");

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
    }

    @Test
    public void testDeleteWhitelist() {


    }

}
