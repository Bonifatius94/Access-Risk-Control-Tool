package junit.localdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CriticalAccessQueryTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testQueryCriticalAccessQueries() {

        assert(false);
    }

    @Test
    public void testCreateCriticalAccessQueries() {

        assert(false);
    }

    @Test
    public void testUpdateCriticalAccessQueries() {

        assert(false);
    }

    @Test
    public void testDeleteCriticalAccessQueries() {

        assert(false);
    }

}
