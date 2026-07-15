package test.integration.classmanagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import core.BaseApiTest;
import core.TestDataReader;
import core.TestUtil;

public class MutationDeleteBootcampSubmissionTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationDeleteBootcampSubmissionTest.class);
    private String sid;
    private String submissionId;

    private static final String mutation = """
     mutation deleteBootcampSubmission($id: String!) {
       deleteBootcampSubmission(id: $id) {
         id
       }
     }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: At least one submission exists");
        submissionId = TestDataReader.getValue("created-submission-id-api");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Submission Id", "value", submissionId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P2
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-067 - User can delete a class submission")
    public void deleteBootcampSubmission() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", submissionId);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "deleteBootcampSubmission", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.deleteBootcampSubmission"));
        Assert.assertTrue(jsonPath.get("data.deleteBootcampSubmission") instanceof Map);

        // Validate submission props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.deleteBootcampSubmission");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        logger.info("User can delete a class submission: executed successfully");
    }
}
