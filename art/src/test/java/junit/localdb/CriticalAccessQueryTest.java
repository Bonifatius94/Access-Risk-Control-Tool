package junit.localdb;

import data.entities.*;
import data.localdb.ArtDbContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            // get configuration and sapconfiguration
            Configuration config = context.getConfigs(false).stream().findFirst().get();
            SapConfiguration sapconfig = context.getSapConfigs(false).stream().findFirst().get();

            // create the query
            CriticalAccessQuery query = new CriticalAccessQuery();
            query.setConfig(config);
            query.setSapConfig(sapconfig);
            String author = "querytest";
            query.setCreatedBy(author);

            // insert query into database
            context.createSapQuery(query);

            // query updated data
            List<CriticalAccessQuery> queries = context.getFilteredCriticalAccessQueries(false, null, null, null, null);

            // check if new query was inserted
            ret = queries.stream().anyMatch(x -> x.getConfig().equals(config) && x.getSapConfig().equals(sapconfig) && x.getCreatedBy().equals(author));

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

            // get configuration and sapconfiguration
            Configuration config = context.getConfigs(false).stream().findFirst().get();
            SapConfiguration sapconfig = context.getSapConfigs(false).stream().findFirst().get();

            // create the query
            CriticalAccessQuery query = new CriticalAccessQuery();
            query.setConfig(config);
            query.setSapConfig(sapconfig);
            String author = "QUERYTEST";
            query.setCreatedBy(author);

            // insert query into database
            context.createSapQuery(query);

            // query updated data
            List<CriticalAccessQuery> queries = context.getFilteredCriticalAccessQueries(false, null, null, null, null);

            // check if new query was inserted
            ret = queries.stream().anyMatch(x -> x.getConfig().equals(config) && x.getCreatedBy().equals(author));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    @Disabled
    public void testUpdateCriticalAccessQueries() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // get configuration and sapconfiguration
            Configuration config = context.getConfigs(false).stream().findFirst().get();
            SapConfiguration sapconfig = context.getSapConfigs(false).stream().findFirst().get();

            // create the query
            CriticalAccessQuery query = new CriticalAccessQuery();
            query.setConfig(config);
            query.setSapConfig(sapconfig);
            String author = "querytest";
            query.setCreatedBy(author);

            // insert query into database
            context.createSapQuery(query);

            // query updated data
            List<CriticalAccessQuery> queries = context.getFilteredCriticalAccessQueries(true, null, null, null, null);

            // check if new query was inserted
            ret = queries.stream().anyMatch(x -> x.getSapConfig().equals(sapconfig));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    @Disabled
    public void testDeleteCriticalAccessQueries() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // get configuration and sapconfiguration
            Configuration config = context.getConfigs(false).stream().findFirst().get();
            SapConfiguration sapconfig = context.getSapConfigs(false).stream().findFirst().get();

            // create the query
            CriticalAccessQuery query = new CriticalAccessQuery();
            query.setConfig(config);
            query.setSapConfig(sapconfig);
            String author = "querytest";
            query.setCreatedBy(author);

            // insert query into database
            context.createSapQuery(query);

            // query updated data
            List<CriticalAccessQuery> queries = context.getFilteredCriticalAccessQueries(false, null, null, null, null);

            // check if new query was inserted
            ret = queries.stream().anyMatch(x -> x.getConfig().equals(config) && x.getSapConfig().equals(sapconfig) && x.getCreatedBy().equals(author));

            //delete query
            //TODO: find operation

            //check if query was deleted

            //TODO: ask if queries should be deleteable

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assert (false);
    }

}
