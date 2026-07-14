package test.integration.classmanagement;

import java.util.ArrayList;
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

public class MutationCreateBootcampContentTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationCreateBootcampContentTest.class);
    private String contentId;
    private String sid;
    private String classId;

    // Test Data
    private String contentTitle;
    private String contentDesc;
    private String liveClassDuration;
    private String contentLinkMeeting;
    private String contentPreTestUrl;
    private String checkInKey;
    private String checkOutKey;
    private String invalidUrl;

    private static final String mutation = """
    mutation createBootcampContent($input: InputBootcampContent!) {
      createBootcampContent(input: $input) {
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
        contentTitle = TestDataReader.getValue("valid-content-title");
        contentDesc = TestDataReader.getValue("content-desc");
        liveClassDuration = TestDataReader.getValue("content-live-class-duration");
        contentLinkMeeting = TestDataReader.getValue("valid-link");
        contentPreTestUrl = TestDataReader.getValue("valid-link");
        checkInKey = TestDataReader.getValue("check-in-key");
        checkOutKey = TestDataReader.getValue("check-out-key");
        invalidUrl = TestDataReader.getValue("invalid-link");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId),
                Map.of("key", "Content Title", "value", contentTitle),
                Map.of("key", "Content Description", "value", contentDesc),
                Map.of("key", "Live Class Duration", "value", liveClassDuration),
                Map.of("key", "Meeting Link", "value", contentLinkMeeting),
                Map.of("key", "Check In Key", "value", checkInKey),
                Map.of("key", "Check Out Key", "value", checkOutKey),
                Map.of("key", "Invalid Url", "value", invalidUrl)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-014 - User can create class content")
    public void createBootcampContent() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("bootcampId", classId);
        input.put("title", contentTitle);
        input.put("descriptions", "<p>" + contentDesc + "</p>");
        input.put("videoPreClassUrl", "");
        input.put("videoPostClassUrl", "");
        input.put("liveClassTime", getDateTimeFromNow(7));
        input.put("liveClassDuration", Integer.parseInt(liveClassDuration));
        input.put("preTestUrl", contentPreTestUrl);
        input.put("isAttendanceCounted", true);
        input.put("checkInKey", checkInKey);
        input.put("enableCheckIn", true);
        input.put("checkOutKey", checkOutKey);
        input.put("enableCheckOut", true);
        input.put("zoomUrl", contentLinkMeeting);
        input.put("learningResourses", new ArrayList<>());
        input.put("videoAddOns", new ArrayList<>());

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "createBootcampContent", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.createBootcampContent"));
        Assert.assertTrue(jsonPath.get("data.createBootcampContent") instanceof Map);

        // Validate content props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.createBootcampContent");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        contentId = (String) dataObj.get("id");

        logger.info("User can create class content: executed successfully");
    }

    // Negative Test | P3 | Invalid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-017 - User cant add class content with invalid pre test url")
    public void createBootcampContentWithInvalidPreTestUrl() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("bootcampId", classId);
        input.put("title", contentTitle);
        input.put("descriptions", "<p>" + contentDesc + "</p>");
        input.put("videoPreClassUrl", "");
        input.put("videoPostClassUrl", "");
        input.put("liveClassTime", getDateTimeFromNow(7));
        input.put("liveClassDuration", Integer.parseInt(liveClassDuration));
        input.put("preTestUrl", invalidUrl);
        input.put("isAttendanceCounted", true);
        input.put("checkInKey", checkInKey);
        input.put("enableCheckIn", true);
        input.put("checkOutKey", checkOutKey);
        input.put("enableCheckOut", true);
        input.put("zoomUrl", contentLinkMeeting);
        input.put("learningResourses", new ArrayList<>());
        input.put("videoAddOns", new ArrayList<>());

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "createBootcampContent", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        TestUtil.validateAPIFailed(jsonPath, "Pretest Url harus menggunakan url yang valid");

        logger.info("User cant add class content with invalid pre test url: executed successfully");
    }

    // Negative Test | P2 | Invalid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-018 - User cant add class content with invalid class meeting link")
    public void createBootcampContentWithInvalidClassMeetingUrl() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("bootcampId", classId);
        input.put("title", contentTitle);
        input.put("descriptions", "<p>" + contentDesc + "</p>");
        input.put("videoPreClassUrl", "");
        input.put("videoPostClassUrl", "");
        input.put("liveClassTime", getDateTimeFromNow(7));
        input.put("liveClassDuration", Integer.parseInt(liveClassDuration));
        input.put("preTestUrl", contentPreTestUrl);
        input.put("isAttendanceCounted", true);
        input.put("checkInKey", checkInKey);
        input.put("enableCheckIn", true);
        input.put("checkOutKey", checkOutKey);
        input.put("enableCheckOut", true);
        input.put("zoomUrl", invalidUrl);
        input.put("learningResourses", new ArrayList<>());
        input.put("videoAddOns", new ArrayList<>());

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "createBootcampContent", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        TestUtil.validateAPIFailed(jsonPath, "Zoom Url harus menggunakan url yang valid");

        logger.info("User cant add class content with invalid class meeting link: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created content
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("createBootcampContent")) {
            TestDataReader.setValue("created-content-title", contentTitle);
            TestDataReader.setValue("created-content-id", contentId);
        }
    }
}
