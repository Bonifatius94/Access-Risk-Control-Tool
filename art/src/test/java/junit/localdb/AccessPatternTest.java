package junit.localdb;

import data.entities.AccessConditionType;
import data.entities.AccessPattern;
import data.entities.AccessProfileCondition;
import data.localdb.ArtDbContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccessPatternTest {

    @BeforeEach
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

    private static String getDefaultDatabaseFilePath() {

        String currentExeFolder = System.getProperty("user.dir");
        return Paths.get(currentExeFolder, "foo.h2.mv.db").toAbsolutePath().toString();
    }

    private static void deleteFileIfExists(String filePath) {

        File database = new File(filePath);

        if (database.exists()) {
            database.delete();
        }
    }

    @Test
    public void testQueryAccessPattern() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query whitelists
            List<AccessPattern> patterns = context.getPatterns();

            AccessPattern profileAccessPattern = patterns.stream().filter(x -> x.getConditions().size() == 1).findFirst().get();
            AccessProfileCondition profileCondition = profileAccessPattern.getConditions().stream().findFirst().get().getProfileCondition();

            // check if test data was queried successfully
            ret = patterns.size() == 3
                && patterns.stream().allMatch(x -> x.getConditions().size() != 2 || x.getConditions().stream().allMatch(y -> y.getPatternCondition().getProperties().size() == 5))
                && profileCondition.getProfile().equals("SAP_ALL");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    // TODO: implement missing tests

}
