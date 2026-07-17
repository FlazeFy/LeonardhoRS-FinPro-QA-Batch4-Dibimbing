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

public class MutationDeleteBootcampTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationDeleteBootcampTest.class);
    private String sid;
    private String classId;

    private static final String mutation = """
     mutation deleteBootcamp($id: String!) {
       deleteBootcamp(id: $id) {
         id
       }
     }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: At least one class exists");
        classId = TestDataReader.getValue("created-bootcamp-id-api");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-007 - User can delete a class")
    public void deleteBootcamp() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", classId);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "deleteBootcamp", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.deleteBootcamp"));
        Assert.assertTrue(jsonPath.get("data.deleteBootcamp") instanceof Map);

        // Validate announcement props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.deleteBootcamp");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        logger.info("User can delete a class: executed successfully");
    }
}
