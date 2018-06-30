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
    @Disabled
    public void testQueryCriticalAccessQueries() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // get objects for the query constructor
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(1))).findFirst().get();
            SapConfiguration sapconfig = context.getSapConfigs(false).stream().findFirst().get();

            // create the query
            CriticalAccessQuery query = new CriticalAccessQuery();

            // insert query into database
            context.createSapQuery(query);

            // query updated data
            List<CriticalAccessQuery> queries = context.getFilteredCriticalAccessQueries(false, null, null, null, null);

            // check if new query was inserted
            ret = queries.stream().anyMatch(x -> x.getConfig().equals(config) && x.getSapConfig().equals(sapconfig));

            // execute query


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    @Disabled
    public void testCreateCriticalAccessQueries() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // get objects for the query constructor
            Configuration config = context.getConfigs(false).stream().filter(x -> x.getId().equals(new Integer(1))).findFirst().get();
            SapConfiguration sapconfig = context.getSapConfigs(false).stream().findFirst().get();
            AccessPattern pattern = config.getPatterns().stream().filter(x -> x.getId().equals(new Integer(3))).findFirst().get();
            Set entries = new HashSet();
            entries.add(new CriticalAccessEntry(pattern, "raboof"));

            // create the query
            CriticalAccessQuery query = new CriticalAccessQuery(config, sapconfig, entries);

            // insert query into database
            context.createSapQuery(query);

            // query updated data
            List<CriticalAccessQuery> queries = context.getFilteredCriticalAccessQueries(false, null, null, null, null);

            // check if new query was inserted
            ret = queries.stream().anyMatch(x -> x.getConfig().equals(config) && x.getSapConfig().equals(sapconfig) && x.getEntries().equals(entries));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

}
