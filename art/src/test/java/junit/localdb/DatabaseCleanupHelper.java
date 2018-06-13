package junit.localdb;

import data.localdb.ArtDbContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class DatabaseCleanupHelper {

    public void cleanupDatabase() throws Exception {

        System.out.print("cleaning up datebase ...");
        deleteFileIfExists(getDefaultDatabaseFilePath());
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

    private String getDefaultDatabaseFilePath() {

        String currentExeFolder = System.getProperty("user.dir");
        return Paths.get(currentExeFolder, "foo.h2.mv.db").toAbsolutePath().toString();
    }

    private void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

}
