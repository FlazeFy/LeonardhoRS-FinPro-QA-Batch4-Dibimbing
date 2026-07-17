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
import test.integration.dashboard.QueryMyCompanyTest;
import core.BaseApiTest;
import core.TestDataReader;
import core.TestUtil;

public class QueryAssignedMentorTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompanyTest.class);
    private String sid;
    private String classId;
    private String mentorName;

    private static final String query = """
        query AssignedMentor($bootcampId: String!, $query: UserBaseQuery) {
          assignedMentor(bootcampId: $bootcampId, query: $query) {
            id name employeeRole employeeId countBootcampSubmissionAssigned(bootcampId: $bootcampId) countBootcampQuizAssigned(bootcampId: $bootcampId) isMentor division { id name }
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

    private void validateMentorResponse(List<Map<String, Object>> mentors) {
        // Validate mentors props
        // Validate each fields data type
        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(mentors, stringFields, "string", false);

        List<String> stringNullableFields = List.of("name", "employeeId", "employeeRole");
        TestUtil.validateColumn(mentors, stringNullableFields, "string", true);

        List<String> intFields = List.of("countBootcampSubmissionAssigned", "countBootcampQuizAssigned");
        TestUtil.validateColumn(mentors, intFields, "number", false);

        List<String> booleanFields = List.of("isMentor");
        TestUtil.validateColumn(mentors, booleanFields, "boolean", true);

        // Validate each fields not empty / not whitespace only
        // check this again .....
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(mentors, notEmptyStringFields);

        // Validate division
        for (Map<String, Object> mt: mentors) {
            Object divisionObj = mt.get("division");
            if (divisionObj == null) continue;

            List<String> stringDivisionFields = List.of("id", "name");
            TestUtil.validateColumn(mt, stringDivisionFields, "string", false);
        }
    }

    // Positive Test | P1
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-034 - User can view assigned class mentor")
    public void assignedMentor() {
        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("search", "");
        param.put("page", 1);
        param.put("limit", 5);

        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("query", param);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "assignedMentor", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.assignedMentor"));
        List<Map<String, Object>> mentors = jsonPath.getList("data.assignedMentor");

        validateMentorResponse(mentors);

        logger.info("User can view assigned class mentor: executed successfully");
    }

    // Positive Test | P3 | Valid
    @Test(priority = 2, groups = {"api-test"}, description = "TC-CLMG-036 - User can search assigned class mentor with valid keyword")
    public void assignedMentorWithValidKeyword() {
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
                "assignedMentor", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.assignedMentor"));
        List<Map<String, Object>> mentors = jsonPath.getList("data.assignedMentor");

        validateMentorResponse(mentors);

        for (Map<String, Object> mt: mentors) {
            String actualName = (String)mt.get("name");

            Assert.assertTrue(actualName.contains(mentorName), "Mentor name must be related with search keyword");
        }

        logger.info("User can search assigned class mentor with valid keyword: executed successfully");
    }
}
