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
import core.BaseApiTest;
import core.TestDataReader;
import core.TestUtil;

import static core.DataGenerator.getDateTimeFromNow;

public class MutationCreateBootcampQuizTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationCreateBootcampQuizTest.class);

    private String sid;
    private String quizIdContent;
    private String quizIdClass;
    private String classId;

    // Test Data
    private String quizTitle;
    private String meetingLink;
    private String quizTimer;
    private String contentId;

    private static final String mutation = """
    mutation createBootcampQuiz($input: InputBootcampQuiz!) {
      createBootcampQuiz(input: $input) {
        id
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: User already select a class");
        classId = TestDataReader.getValue("created-bootcamp-id-api");
        quizTitle = TestDataReader.getValue("quiz-title");
        meetingLink = TestDataReader.getValue("valid-link");
        quizTimer = TestDataReader.getValue("quiz-timer");
        contentId = TestDataReader.getValue("created-content-id");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId),
                Map.of("key", "Quiz Title", "value", quizTitle),
                Map.of("key", "Meeting Link", "value", meetingLink),
                Map.of("key", "Quiz Timer", "value", quizTimer),
                Map.of("key", "Content Id", "value", contentId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-048 - User can create class quiz")
    public void createBootcampQuizClassTest() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("bootcampId", classId);
        input.put("contentId", null);
        input.put("title", quizTitle);
        input.put("startDate", getDateTimeFromNow(2));
        input.put("type", "preTest");
        input.put("timer", Integer.parseInt(quizTimer));
        input.put("deadline", null);
        input.put("meetingLink", meetingLink);
        input.put("needGradingConfirmation", false);
        input.put("mentorId", null);

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "createBootcampQuiz", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.createBootcampQuiz"));
        Assert.assertTrue(jsonPath.get("data.createBootcampQuiz") instanceof Map);

        // Validate quiz props
        Map<String, Object> dataObj = jsonPath.getMap("data.createBootcampQuiz");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        quizIdClass = (String) dataObj.get("id");

        logger.info("User can create class quiz: executed successfully");
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-054 - User can add content test with valid data")
    public void createBootcampQuizContentTest() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("bootcampId", classId);
        input.put("contentId", contentId);
        input.put("title", quizTitle);
        input.put("startDate", getDateTimeFromNow(2));
        input.put("type", "preTest");
        input.put("timer", Integer.parseInt(quizTimer));
        input.put("deadline", null);
        input.put("meetingLink", meetingLink);
        input.put("needGradingConfirmation", false);
        input.put("mentorId", null);

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "createBootcampQuiz", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.createBootcampQuiz"));
        Assert.assertTrue(jsonPath.get("data.createBootcampQuiz") instanceof Map);

        // Validate quiz props
        Map<String, Object> dataObj = jsonPath.getMap("data.createBootcampQuiz");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        quizIdContent = (String) dataObj.get("id");

        logger.info("User can add content test with valid data: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created quiz
        if (result.getStatus() == ITestResult.SUCCESS) {
            if (result.getMethod().getMethodName().equals("createBootcampQuizClassTest")) {
                TestDataReader.setValue("created-quiz-id-class-api", quizIdClass);
                TestDataReader.setValue("created-quiz-title-class-api", quizTitle);
            }

            if (result.getMethod().getMethodName().equals("createBootcampQuizContentTest")) {
                TestDataReader.setValue("created-quiz-id-content-api", quizIdContent);
                TestDataReader.setValue("created-quiz-title-content-api", quizTitle);
            }
        }
    }
}
