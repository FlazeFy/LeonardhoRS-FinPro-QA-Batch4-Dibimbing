package test.integration.classmanagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.integration.dashboard.QueryMyCompanyTest;
import core.BaseApiTest;
import core.TestDataReader;
import core.TestUtil;

public class QueryUnassignedMentorTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompanyTest.class);
    private String sid;
    private String classId;
    private String mentorName;
    private String mentorId1;
    private String mentorId2;
    private String mentorId3;

    private static final String query = """
        query UnassignedMentor($bootcampId: String!, $query: UserBaseQuery) {
          unassignedMentor(bootcampId: $bootcampId, query: $query) {
            id name employeeRole employeeId isMentor division { id name }
          }
        }
        """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: User already select a class");
        classId = TestDataReader.getValue("created-bootcamp-id-api");

        logger.info("Pre-Condition: At least one mentor assigned");
        mentorName = TestDataReader.getValue("valid-search-mentor-name");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId),
                Map.of("key", "Mentor Name", "value", mentorName)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P3 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-037 - User can search class mentor in the Add Mentor pop-up")
    public void unassignedMentorWithValidKeyword() {
        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("search", mentorName);
        param.put("page", 1);
        param.put("limit", 5);

        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("query", param);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "unassignedMentor", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.unassignedMentor"));
        List<Map<String, Object>> mentors = jsonPath.getList("data.unassignedMentor");

        // Validate mentors props
        // Validate each fields data type
        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(mentors, stringFields, "string", false);

        List<String> stringNullableFields = List.of("name", "employeeId", "employeeRole");
        TestUtil.validateColumn(mentors, stringNullableFields, "string", true);

        List<String> booleanFields = List.of("isMentor");
        TestUtil.validateColumn(mentors, booleanFields, "boolean", true);

        // Validate each fields not empty / not whitespace only
        // check this again .....
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(mentors, notEmptyStringFields);

        // Validate division and name
        for (Map<String, Object> mt: mentors) {
            String actualName = (String)mt.get("name");
            Assert.assertTrue(actualName.contains(mentorName), "Mentor name must be related with search keyword");

            Object divisionObj = mt.get("division");
            if (divisionObj == null) continue;

            List<String> stringDivisionFields = List.of("id", "name");
            TestUtil.validateColumn(mt, stringDivisionFields, "string", false);
        }

        mentorId1 = (String)mentors.get(0).get("id");
        mentorId2 = (String)mentors.get(1).get("id");
        mentorId3 = (String)mentors.get(2).get("id");

        logger.info("User can search class mentor in the Add Mentor pop-up: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created announcement
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("unassignedMentorWithValidKeyword")) {
            TestDataReader.setValue("mentor-id-1", mentorId1);
            TestDataReader.setValue("mentor-id-2", mentorId2);
            TestDataReader.setValue("mentor-id-3", mentorId3);
        }
    }
}
