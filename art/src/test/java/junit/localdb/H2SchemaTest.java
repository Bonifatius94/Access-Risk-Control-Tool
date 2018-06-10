package junit.localdb;

import data.localdb.ArtDbContext;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class H2SchemaTest {

    @BeforeEach
    public void cleanupDatabase() {

        // delete database to ensure a clean test environment
        deleteFileIfExists("D:\\TEMP\\foo.h2.mv.db");
    }

    private static void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

    @Test
    public void testCreateDbSchema() {

        // create db context
        // this automatically creates a new database file with the database schema (code-first-approach)
        // the first user becomes db admin

        try (ArtDbContext context = new ArtDbContext("test", "test")) {
            assert(true);
        } catch (Exception ex) {
            assert(false);
        }
    }

}