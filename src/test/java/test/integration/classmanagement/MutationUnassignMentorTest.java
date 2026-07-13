package test.integration.classmanagement;

import core.BaseApiTest;
import core.TestDataReader;
import core.TestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutationUnassignMentorTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationUnassignMentorTest.class);
    private String sid;
    private String userId1;
    private String userId2;
    private String userId3;
    private String classId;

    private static final String mutation = """
     mutation unassignMentor($bootcampId: String!, $userIds: [String!]!) {
       unassignMentor(bootcampId: $bootcampId, userIds: $userIds) {
         id
       }
     }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: At least one mentor exists");

        userId1 = TestDataReader.getValue("mentor-id-1");
        userId2 = TestDataReader.getValue("mentor-id-2");
        userId3 = TestDataReader.getValue("mentor-id-3");

        logger.info("Pre-Condition: User already select a class");
        classId = TestDataReader.getValue("class-id");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId),
                Map.of("key", "User Id 1", "value", userId1),
                Map.of("key", "User Id 2", "value", userId2),
                Map.of("key", "User Id 3", "value", userId3)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    private void validateAssignMentor(Map<String, Object> dataObj) {
        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);
    }

    // Positive Test | P1
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-044 - User can remove assigned mentor from class")
    public void assignMentor() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("userIds", List.of(userId1, userId2, userId3));

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "unassignMentor", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.unassignMentor"));
        Assert.assertTrue(jsonPath.get("data.unassignMentor") instanceof Map);

        // Validate assign mentor
        Map<String, Object> dataObj = jsonPath.getMap("data.unassignMentor");
        validateAssignMentor(dataObj);

        logger.info("User can remove assigned mentor from class: executed successfully");
    }
}
