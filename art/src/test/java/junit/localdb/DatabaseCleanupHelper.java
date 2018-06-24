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

        String databasePath;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {
            databasePath = context.getFilePath();
        }

        File database = new File(databasePath);

        if (database.exists()) {
            database.delete();
        }
    }

    private void createDatabaseWithTestDataSeed() throws Exception {

        String scriptPath = getClass().getClassLoader().getResource("scripts/create_test_data_seed.sql").getFile();

        try (ArtDbContext context = new ArtDbContext("test", "test")) {
            context.executeScript(scriptPath);
        }
    }

}
