package junit.localdb;

import data.localdb.ArtDbContext;
import org.junit.Test;

public class H2Test {

    @Test
    public void testCreateSchema() {

        boolean result = false;

        // create db context
        // this automatically creates a new database file with the database schema (code-first-approach)
        // the first user becomes db admin
        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            System.out.println("database schema creation successful");
            result = true;

        } catch (Exception ex) {

            System.out.println("database schema creation failed");
            ex.printStackTrace();
        }

        assert(result);
    }

}