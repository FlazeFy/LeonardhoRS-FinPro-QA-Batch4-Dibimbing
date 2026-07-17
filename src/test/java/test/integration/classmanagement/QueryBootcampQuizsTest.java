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

public class QueryBootcampQuizsTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryBootcampQuizsTest.class);

    private String sid;
    private String classId;

    // Test Data
    private String quizTitle;

    private static final String query = """
    query bootcampQuizs($bootcampId: String!, $param: BootcampQuizBaseQuery, $bootcampContentId: String) {
      bootcampQuizs(bootcampId: $bootcampId, param: $param, bootcampContentId: $bootcampContentId) {
        id startDate timer type title deadline needGradingConfirmation meetingLink
        isFinishGrading isDone mentorId
        content { id title liveClassTime }
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: User already select a class");
        classId = TestDataReader.getValue("created-bootcamp-id-api");

        // Test Data
        quizTitle = TestDataReader.getValue("created-quiz-title-class");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId),
                Map.of("key", "Quiz Title", "value", quizTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    private void validateBootcampQuizs(List<Map<String, Object>> quizzes) {
        // Validate data types
        List<String> stringFields = List.of("id");
        List<String> stringNullableFields = List.of("startDate", "type", "title", "deadline", "meetingLink", "mentorId");
        List<String> intFields = List.of("timer");
        List<String> booleanFields = List.of("needGradingConfirmation", "isFinishGrading", "isDone");
        TestUtil.validateColumn(quizzes, stringFields, "string", false);
        TestUtil.validateColumn(quizzes, stringNullableFields, "string", true);
        TestUtil.validateColumn(quizzes, intFields, "number", false);
        TestUtil.validateColumn(quizzes, booleanFields, "boolean", false);
        // Validate not empty
        TestUtil.validateNotEmptyString(quizzes, List.of("id"));

        // Validate content
        for (Map<String, Object> quiz : quizzes) {
            Object contentObj = quiz.get("content");
            if (contentObj != null) {
                Map<String, Object> content = (Map<String, Object>) contentObj;

                List<String> stringContentFields = List.of("id");
                List<String> stringNullableContentFields = List.of("title", "liveClassTime");
                TestUtil.validateColumn(content, stringContentFields, "string", false);
                TestUtil.validateColumn(content, stringNullableContentFields, "string", true);
                TestUtil.validateNotEmptyString(content, List.of("id"));
            }
        }
    }

    // Positive Test | P2
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-045 - User can view class test")
    public void bootcampQuizs() {
        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("search", "");
        param.put("page", 1);
        param.put("limit", 5);
        param.put("orderBy", "DESC");
        param.put("orderColumn", "createdAt");

        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("param", param);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "bootcampQuizs", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.bootcampQuizs"));
        List<Map<String, Object>> quizzes = jsonPath.getList("data.bootcampQuizs");

        validateBootcampQuizs(quizzes);

        logger.info("User can view class test: executed successfully");
    }

    // Positive Test | P3 | Valid
    @Test(priority = 2, groups = {"api-test"}, description = "TC-CLMG-047 - User can search class test with valid keyword")
    public void bootcampQuizsWithValidKeyword() {
        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("search", quizTitle);
        param.put("page", 1);
        param.put("limit", 5);
        param.put("orderBy", "DESC");
        param.put("orderColumn", "createdAt");

        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("param", param);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "bootcampQuizs", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.bootcampQuizs"));
        List<Map<String, Object>> quizzes = jsonPath.getList("data.bootcampQuizs");

        validateBootcampQuizs(quizzes);

        // Validate content
        for (Map<String, Object> quiz : quizzes) {
            String expectedQuizTitle = quizTitle.toLowerCase();
            String actualQuizTitle = ((String) quiz.get("title")).toLowerCase();

            // Make sure quiz title related to search keyword
            Assert.assertTrue(actualQuizTitle.contains(expectedQuizTitle), "Expected " + expectedQuizTitle + " to contain in " + actualQuizTitle);
        }

        logger.info("User can search class test with valid keyword: executed successfully");
    }
}
