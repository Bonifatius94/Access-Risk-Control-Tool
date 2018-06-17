package junit.localdb;

import data.entities.Whitelist;
import data.entities.WhitelistEntry;
import data.localdb.ArtDbContext;

import io.msoffice.excel.WhitelistImportHelper;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("all")
public class WhitelistTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testQueryWhitelist() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists
            List<Whitelist> whitelists = context.getWhitelists(false);

            // check if test data was queried successfully
            ret = whitelists.size() == 1 && whitelists.stream().allMatch(x -> x.getEntries().size() == 7);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    @Disabled
    public void testCreateWhitelist() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists before insertion
            List<Whitelist> whitelists = context.getWhitelists(false);
            int countBefore = (whitelists != null) ? whitelists.size() : 0;

            // insert a whitelist
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            whitelist.setDescription("a test description");
            System.out.println(whitelist);
            context.createWhitelist(whitelist);

            // query whitelists after insertion
            whitelists = context.getWhitelists(false);
            System.out.println(whitelist);
            int countAfter = (whitelists != null) ? whitelists.size() : 0;

            // compare counts
            ret = (countBefore + 1 == countAfter && whitelists.stream().allMatch(x -> x.getEntries().size() == 7));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    @Disabled
    public void testUpdateWhitelist() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query first whitelist from database
            Whitelist whitelist = context.getWhitelists(false).get(0);
            int whitelistId = whitelist.getId();

            // make changes
            final String newDescription = "another description";
            whitelist.setDescription(newDescription);

            List<WhitelistEntry> entries = new ArrayList<>(whitelist.getEntries());
            WhitelistEntry entry1 = entries.get(0);
            int entry1Id = entry1.getId();
            entries.remove(1);

            final String newUsername = "foo123";
            final String newUsecaseId = "blabla";
            entry1.setUsername(newUsername);
            entry1.setUsecaseId(newUsecaseId);

            whitelist.setEntries(entries);

            // update whitelist
            context.updateWhitelist(whitelist);

            // query whitelist
            whitelist = context.getWhitelists(false).stream().filter(x -> x.getId().equals(whitelistId)).findFirst().orElse(null);
            entry1 = whitelist.getEntries().stream().filter(x -> x.getId().equals(entry1Id)).findFirst().get();

            ret = newDescription.equals(whitelist.getDescription())
                && whitelist.getEntries().size() == 6
                && entry1.getUsecaseId().equals(newUsecaseId) && entry1.getUsername().equals(newUsername);

            // TODO: add a test for archiving logic (old whitelist was used by a sap query)

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testDeleteWhitelist() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists
            List<Whitelist> whitelists = context.getWhitelists(false);
            Whitelist whitelist = whitelists.get(0);
            int id = whitelist.getId();

            // delete whitelist
            context.deleteWhitelist(whitelist);

            // query whitelist again. check if everything was deleted
            whitelist = context.getWhitelists(false).stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);

            // check if test data was queried successfully
            ret = whitelist == null;

            // TODO: write a test for archiving logic (old whitelist was used by a sap query)

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

}
