package junit.localdb;

import data.localdb.ArtDbContext;

import java.io.File;

public class DatabaseCleanupHelper {

    public void cleanupDatabase() throws Exception {

        System.out.print("cleaning up datebase ...");

        // delete old database and create new database with test data seed
        deleteDatabase();
        createDatabaseWithTestDataSeed();

        System.out.println(" done");
    }

    private void deleteDatabase() throws Exception {

        String databasePath = "art.h2.mv.db";

        //try (ArtDbContext context = new ArtDbContext("test", "test")) {
        //    databasePath = context.getFilePath();
        //}

        File database = new File(databasePath);

        if (database.exists()) {
            database.delete();
        }
    }

    private void createDatabaseWithTestDataSeed() throws Exception {

        try (ArtDbContext context = new ArtDbContext("test", "test")) {
            context.executeScript("scripts/create_test_data_seed.sql");
        }
    }

}
