package junit.localdb;

import data.localdb.ArtDbContext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseCleanupHelper {

    public void cleanupDatabase() throws Exception {

        System.out.print("cleaning up datebase ...");

        // delete old database and create new database with test data seed
        deleteDatabase();
        createDatabaseWithTestDataSeed();

        System.out.println(" done");
    }

    private void deleteDatabase() {

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

        String currentExeFolder = System.getProperty("user.dir");
        Path scriptPath = Paths.get(currentExeFolder, "target", "test-classes", "scripts", "create_test_data_seed.sql");

        try (ArtDbContext context = new ArtDbContext("test", "test")) {
            context.executeScript(scriptPath.toAbsolutePath().toString());
        }
    }

}
