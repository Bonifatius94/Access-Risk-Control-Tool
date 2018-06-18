package junit.localdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CriticalAccessQueryTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    @Disabled
    public void testQueryCriticalAccessQueries() {

        assert(false);
    }

    @Test
    @Disabled
    public void testCreateCriticalAccessQueries() {

        assert(false);
    }

    @Test
    @Disabled
    public void testUpdateCriticalAccessQueries() {

        assert(false);
    }

    @Test
    @Disabled
    public void testDeleteCriticalAccessQueries() {

        assert(false);
    }

}
