package junit.localdb;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.ConditionLinkage;
import data.localdb.ArtDbContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class AccessPatternTest {

    @BeforeEach
    public void cleanupDatabase() throws Exception {
        new DatabaseCleanupHelper().cleanupDatabase();
    }

    @Test
    public void testQueryAccessPattern() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns
            List<AccessPattern> patterns = context.getPatterns(false);

            AccessPattern profileAccessPattern = patterns.stream().filter(x -> x.getId().equals(1)).findFirst().get();
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

    @Test
    public void testCreateAccessPatternWithPatternConditions() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create pattern data objects
            final String usecaseId = "1.A";
            final String description = "a test description";

            AccessPattern pattern = new AccessPattern(usecaseId, description, new ArrayList<>(), ConditionLinkage.And);

            pattern.setConditions(Arrays.asList(
                new AccessCondition(
                    pattern,
                    new AccessPatternCondition(Arrays.asList(
                        new AccessPatternConditionProperty("S_TCODE", "TCD", "SCCL", null, null, null),
                        new AccessPatternConditionProperty("S_ADMI_FCD", "S_ADMI_FCD", "T000", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "ACTVT", "02", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "DICBERCLS", "\"*\"", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_CLI", "CLIIDMAINT", "X", null, null, null)
                    ))
                ),
                new AccessCondition(
                    pattern,
                    new AccessPatternCondition(Arrays.asList(
                        new AccessPatternConditionProperty("S_TCODE", "TCD", "SCCL", null, null, null),
                        new AccessPatternConditionProperty("S_ADMI_FCD", "S_ADMI_FCD", "T000", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "ACTVT", "02", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_DIS", "DICBERCLS", "\"*\"", null, null, null),
                        new AccessPatternConditionProperty("S_TABU_CLI", "CLIIDMAINT", "X", null, null, null)
                    ))
                )
            ));

            // insert pattern into database
            context.createPattern(pattern);

            // query database
            List<AccessPattern> patterns = context.getPatterns(false);

            // check if new pattern was inserted
            ret = patterns.size() == 4
                && patterns.stream().anyMatch(
                       x -> x.getConditions().size() == 2
                    && x.getUsecaseId().equals(usecaseId)
                    && x.getDescription().equals(description)
                    && x.getLinkage() == ConditionLinkage.And
                );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testCreateAccessPatternWithProfileCondition() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // create pattern data objects
            final String usecaseId = "3.A";
            final String description = "a test description";
            final String profile = "SAP_ALL";

            AccessPattern pattern = new AccessPattern(usecaseId, description, new AccessCondition());
            pattern.setConditions(Arrays.asList(new AccessCondition(pattern, new AccessProfileCondition(profile))));

            // insert pattern into database
            context.createPattern(pattern);

            // query database
            List<AccessPattern> patterns = context.getPatterns(false);

            // check if new pattern was inserted
            ret = patterns.size() == 4
                && patterns.stream().anyMatch(
                x -> x.getConditions().size() == 1
                    && x.getUsecaseId().equals(usecaseId)
                    && x.getDescription().equals(description)
                    && x.getLinkage() == ConditionLinkage.None
                    && x.getConditions().stream().anyMatch(y -> y.getProfileCondition().getProfile().equals(profile))
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testUpdatePatternWithProfileCondition() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns
            List<AccessPattern> patterns = context.getPatterns(false);
            AccessPattern profilePattern = patterns.stream().filter(x -> x.getConditions().size() == 1).findFirst().get();
            AccessProfileCondition profileCondition = profilePattern.getConditions().stream().findFirst().get().getProfileCondition();
            AccessPattern multiConditionPattern = patterns.stream().filter(x -> x.getConditions().size() == 2).findFirst().get();
            Integer profilePatternId = profilePattern.getId();
            Integer multiConditionPatternId = multiConditionPattern.getId();

            // apply changes to profile condition
            final String newProfile = "SAP_NEW";
            final String newDescription = "another description";
            profilePattern.setLinkage(ConditionLinkage.Or);
            profilePattern.setDescription(newDescription);
            profileCondition.setProfile(newProfile);

            // update database
            context.updatePattern(profilePattern);

            // query data again
            patterns = context.getPatterns(false);
            profilePattern = patterns.stream().filter(x -> x.getId().equals(profilePatternId)).findFirst().get();
            profileCondition = profilePattern.getConditions().stream().findFirst().get().getProfileCondition();

            // check if test data was queried successfully
            ret = patterns.size() == 3
                && profilePattern.getDescription().equals(newDescription) && profilePattern.getLinkage() == ConditionLinkage.Or
                && profileCondition.getProfile().equals(newProfile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    @Disabled
    public void testUpdatePatternWithProfileConditionWithArchiving() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns
            List<AccessPattern> patterns = context.getPatterns(false);
            AccessPattern profilePattern = patterns.stream().filter(x -> x.getConditions().size() == 1).findFirst().get();
            AccessProfileCondition profileCondition = profilePattern.getConditions().stream().findFirst().get().getProfileCondition();
            Integer profilePatternId = profilePattern.getId();

            // apply changes to profile condition
            final String newProfile = "SAP_NEW";
            final String newDescription = "another description";
            profilePattern.setLinkage(ConditionLinkage.Or);
            profilePattern.setDescription(newDescription);
            profileCondition.setProfile(newProfile);

            // update database
            context.updatePattern(profilePattern);

            // query data again
            patterns = context.getPatterns(false);
            profilePattern = patterns.stream().filter(x -> x.getId().equals(profilePatternId)).findFirst().get();
            profileCondition = profilePattern.getConditions().stream().findFirst().get().getProfileCondition();

            // TODO: change success condition (this case goes into archiving logic)

            // check if test data was queried successfully
            ret = patterns.size() == 3
                && profilePattern.getDescription().equals(newDescription) && profilePattern.getLinkage() == ConditionLinkage.Or
                && profileCondition.getProfile().equals(newProfile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    @Test
    public void testUpdatePatternWithPatternCondition() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns
            List<AccessPattern> patterns = context.getPatterns(false);
            AccessPattern pattern = patterns.stream().filter(x -> x.getId().equals(2)).findFirst().get();
            Integer patternId = pattern.getId();

            // apply changes to conditions
            List<AccessCondition> patternConditions = new ArrayList<>(pattern.getConditions());
            AccessCondition conditionToDelete = patternConditions.stream().filter(x -> x.getId().equals(3)).findFirst().get();
            AccessCondition conditionToUpdate = patternConditions.stream().filter(x -> x.getId().equals(2)).findFirst().get();
            patternConditions.remove(conditionToDelete);
            AccessPatternConditionProperty propertyToUpdate = conditionToUpdate.getPatternCondition().getProperties().stream().filter(x -> x.getId().equals(1)).findFirst().get();
            AccessPatternConditionProperty propertyToDelete = conditionToUpdate.getPatternCondition().getProperties().stream().filter(x -> x.getId().equals(2)).findFirst().get();
            final String newValue4 = "foobar";
            propertyToUpdate.setValue4(newValue4);
            conditionToUpdate.getPatternCondition().getProperties().remove(propertyToDelete);
            pattern.setConditions(patternConditions);
            Integer conditionId = conditionToUpdate.getId();

            // update database
            context.updatePattern(pattern);

            // query data again
            patterns = context.getPatterns(false);
            pattern = patterns.stream().filter(x -> x.getId().equals(patternId)).findFirst().get();

            // check if test data was queried successfully
            ret = patterns.size() == 3
                && pattern.getConditions().size() == 1
                && pattern.getConditions().stream().anyMatch(x ->
                x.getId().equals(conditionId)
                    && x.getPatternCondition().getProperties().stream().anyMatch(y -> y.getId().equals(propertyToUpdate.getId()) && newValue4.equals(y.getValue4()))
                    && x.getPatternCondition().getProperties().stream().noneMatch(y -> y.getId().equals(propertyToDelete.getId()))
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // TODO: add another test for evaluating archiving logic on update

        assert(ret);
    }

    /*@Test
    public void testUpdatePatternWithPatternCondition() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns
            List<AccessPattern> patterns = context.getPatterns(false);
            AccessPattern pattern = patterns.stream().filter(x -> x.getConditions().size() == 2).findFirst().get();
            Integer patternId = pattern.getId();

            // apply changes to conditions
            List<AccessCondition> patternConditions = new ArrayList<>(pattern.getConditions());
            patternConditions.remove(0);
            AccessCondition condition = patternConditions.get(0);
            AccessPatternConditionProperty property = condition.getPatternCondition().getProperties().stream().findFirst().get();
            final String newValue4 = "foobar";
            property.setValue4(newValue4);
            pattern.setConditions(patternConditions);
            Integer propertyId = property.getId();
            Integer conditionId = condition.getId();

            // update database
            context.updatePattern(pattern);

            // query data again
            patterns = context.getPatterns(false);
            pattern = patterns.stream().filter(x -> x.getId().equals(patternId)).findFirst().get();

            // check if test data was queried successfully
            ret = patterns.size() == 3
                && pattern.getConditions().size() == 1
                && pattern.getConditions().stream().anyMatch(x ->
                    x.getId().equals(conditionId)
                    && x.getPatternCondition().getProperties().stream().anyMatch(y -> y.getId().equals(propertyId) && newValue4.equals(y.getValue4()))
                );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // TODO: add another test for evaluating archiving logic on update

        assert(ret);
    }*/

    @Test
    public void testUpdatePatternWithPatternConditionWithArchiving() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns
            List<AccessPattern> patterns = context.getPatterns(false);
            AccessPattern pattern = patterns.stream().filter(x -> x.getConditions().size() == 2).findFirst().get();
            Integer patternId = pattern.getId();

            // apply changes to conditions
            List<AccessCondition> patternConditions = new ArrayList<>(pattern.getConditions());
            patternConditions.remove(0);
            AccessCondition condition = patternConditions.get(0);
            AccessPatternConditionProperty property = condition.getPatternCondition().getProperties().stream().findFirst().get();
            final String newValue4 = "foobar";
            property.setValue4(newValue4);
            pattern.setConditions(patternConditions);
            Integer propertyId = property.getId();
            Integer conditionId = condition.getId();

            // update database
            context.updatePattern(pattern);

            // query data again
            patterns = context.getPatterns(false);
            pattern = patterns.stream().filter(x -> x.getId().equals(patternId)).findFirst().get();

            // check if test data was queried successfully
            ret = patterns.size() == 3
                && pattern.getConditions().size() == 1
                && pattern.getConditions().stream().anyMatch(x ->
                x.getId().equals(conditionId)
                    && x.getPatternCondition().getProperties().stream().anyMatch(y -> y.getId().equals(propertyId) && newValue4.equals(y.getValue4()))
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // TODO: add another test for evaluating archiving logic on update

        assert(ret);
    }

    @Test
    @Disabled
    public void deletePattern() {

        boolean ret = false;

        try (ArtDbContext context = new ArtDbContext("test", "test")) {

            // query patterns
            List<AccessPattern> patterns = context.getPatterns(false);
            AccessPattern profilePattern = patterns.stream().filter(x -> x.getConditions().size() == 1).findFirst().get();
            AccessPattern multiConditionPattern = patterns.stream().filter(x -> x.getConditions().size() == 2).findFirst().get();

            // delete patterns
            context.deletePattern(profilePattern);
            context.deletePattern(multiConditionPattern);

            // query patterns again
            patterns = context.getPatterns(false);

            // check if patterns were deleted
            ret = patterns.size() == 1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

}
