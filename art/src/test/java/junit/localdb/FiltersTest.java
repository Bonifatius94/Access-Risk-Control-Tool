package junit.localdb;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;
import data.entities.Whitelist;
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testWhitelistFilter() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // test wildcard filter: name
            List<Whitelist> whitelists = context.getFilteredWhitelists(true, "Whitelist 1", null, ZonedDateTime.now(), 10);
            ret = whitelists.size() == 1 && whitelists.stream().anyMatch(x -> x.getId().equals(1));

            // test wildcard filter: description
            whitelists = context.getFilteredWhitelists(false, "Test", null, ZonedDateTime.now(), 10);
            ret = ret && whitelists.size() == 1 && whitelists.stream().anyMatch(x -> x.getId().equals(1));

            // test wildcard filter: usecase id (entries)
            whitelists = context.getFilteredWhitelists(true, "2.b", null, ZonedDateTime.now(), 10);
            ret = ret && whitelists.size() == 3 && whitelists.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2, 3));

            // test wildcard filter: username (entries)
            whitelists = context.getFilteredWhitelists(false, "zt2111", null, ZonedDateTime.now(), 10);
            ret = ret && whitelists.size() == 2 && whitelists.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 3));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testConfigFilter() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // test wildcard filter: name
            List<Configuration> configs = context.getFilteredConfigs(true, "Config 1", null, ZonedDateTime.now(), 10);
            ret = configs.size() == 2 && configs.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 3));

            // test wildcard filter: description
            configs = context.getFilteredConfigs(false, "Test", null, ZonedDateTime.now(), 10);
            ret = ret && configs.size() == 2 && configs.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

            // test wildcard filter: pattern usecase id
            configs = context.getFilteredConfigs(false, "1.a", null, ZonedDateTime.now(), 10);
            ret = ret && configs.size() == 1 && configs.stream().anyMatch(x -> x.getId().equals(2));

            // test wildcard filter: whitelist name
            configs = context.getFilteredConfigs(true, "Whitelist", null, ZonedDateTime.now(), 10);
            ret = ret && configs.size() == 4 && configs.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2, 3, 4));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testSapConfigFilter() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // test wildcard filter: server destination
            List<SapConfiguration> sapConfigs = context.getFilteredSapConfigs(true, "Amazonaws.cOm", null, ZonedDateTime.now(), 10);
            ret = sapConfigs.size() == 2 && sapConfigs.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

            // test wildcard filter: description
            sapConfigs = context.getFilteredSapConfigs(false, "Descr", null, ZonedDateTime.now(), 10);
            ret = ret && sapConfigs.size() == 1 && sapConfigs.stream().anyMatch(x -> x.getId().equals(1));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

    @Test
    public void testCriticalAccessQueryFilter() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // test wildcard filter: config name
            List<CriticalAccessQuery> queries = context.getFilteredCriticalAccessQueries(true, "ConfiG 1", null, ZonedDateTime.now(), 10);
            ret = queries.size() == 2 && queries.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

            // test wildcard filter: config description
            queries = context.getFilteredCriticalAccessQueries(false, "a Test desCription", null, ZonedDateTime.now(), 10);
            ret = ret && queries.size() == 2 && queries.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

            // test wildcard filter: sap config server destination
            queries = context.getFilteredCriticalAccessQueries(false, "amazonaws", null, ZonedDateTime.now(), 10);
            ret = ret && queries.size() == 2 && queries.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

            // test wildcard filter: sap config description
            queries = context.getFilteredCriticalAccessQueries(true, "a deScription", null, ZonedDateTime.now(), 10);
            ret = ret && queries.size() == 2 && queries.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

            // test wildcard filter: violated pattern usecase id
            queries = context.getFilteredCriticalAccessQueries(false, "3.a", null, ZonedDateTime.now(), 10);
            ret = ret && queries.size() == 2 && queries.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

            // test wildcard filter: critical access entry username
            queries = context.getFilteredCriticalAccessQueries(true, "foo123", null, ZonedDateTime.now(), 10);
            ret = ret && queries.size() == 2 && queries.stream().map(x -> x.getId()).collect(Collectors.toList()).containsAll(Arrays.asList(1, 2));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert (ret);
    }

}
