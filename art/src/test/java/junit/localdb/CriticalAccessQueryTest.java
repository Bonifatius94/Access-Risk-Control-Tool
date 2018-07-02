package junit.localdb;

import data.entities.*;
import data.localdb.ArtDbContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CriticalAccessQueryTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testQueryCriticalAccessQueries() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query updated data
            List<CriticalAccessQuery> queries = context.getSapQueries(false);

            // check if new query was inserted
            ret = queries.size() == 2;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testCreateCriticalAccessQueries() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // retrieve config + sap config (used by the new query)
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(1)).findFirst().get();
            SapConfiguration sapConfig = context.getSapConfigs(false).stream().filter(x -> x.getId().equals(1)).findFirst().get();

            // prepare query entries
            AccessPattern violatedPattern = config.getPatterns().stream().filter(x -> x.getId().equals(3)).findFirst().get();
            Set entries = new HashSet();
            final String criticalUser = "raboof";
            CriticalAccessEntry entry = new CriticalAccessEntry(violatedPattern, criticalUser);
            entries.add(entry);

            // create the query
            CriticalAccessQuery query = new CriticalAccessQuery(config, sapConfig, entries);

            // insert query into database
            int queriesCount = context.getSapQueries(true).size();
            context.createSapQuery(query);

            // query updated data
            List<CriticalAccessQuery> queries = context.getSapQueries(false);

            // check if new query was inserted
            ret = queries.stream()
                .anyMatch(x -> x.getConfig().getId().equals(config.getId())
                               && x.getSapConfig().getId().equals(sapConfig.getId())
                               && x.getEntries().stream().anyMatch(y -> y.getAccessPattern().getId().equals(violatedPattern.getId())
                                                                        && y.getUsername().equalsIgnoreCase(criticalUser)))
                && context.getSapQueries(true).size() == queriesCount + 1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

}
