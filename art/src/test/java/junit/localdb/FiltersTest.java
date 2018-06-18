package junit.localdb;

import data.entities.AccessPattern;
import data.localdb.ArtDbContext;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class FiltersTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testPatternFilter() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // test wildcard filter: usecase id
            List<AccessPattern> patterns = context.getFilteredPatterns(true, "1.A", null, ZonedDateTime.now(), 10);
            ret = patterns.size() == 2 && patterns.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(2, 5));

            // test wildcard filter: description
            patterns = context.getFilteredPatterns(false, "copy a client", null, ZonedDateTime.now(), 10);
            ret = ret && patterns.size() == 1 && patterns.stream().anyMatch(x -> x.getId().equals(2));

            // test wildcard filter: auth object
            patterns = context.getFilteredPatterns(false, "S_TCODE", null, ZonedDateTime.now(), 10);
            ret = ret && patterns.size() == 1 && patterns.stream().anyMatch(x -> x.getId().equals(2));

            // test wildcard filter: profile
            patterns = context.getFilteredPatterns(true, "SAP_ALL", null, ZonedDateTime.now(), 10);
            ret = ret && patterns.size() == 2 && patterns.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 4));

            // TODO: add test for start / end of createdAt

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

}
