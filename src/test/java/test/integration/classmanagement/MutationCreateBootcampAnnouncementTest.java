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

public class MutationCreateBootcampAnnouncementTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationCreateBootcampAnnouncementTest.class);
    private String sid;
    private String classId;
    private String announcementTitle;
    private String announcementDesc;
    private String announcementId;

    private static final String mutation = """
    mutation createBootcampAnnouncement($input: InputBootcampAnnouncement!) {
      createBootcampAnnouncement(input: $input) {
        id
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: User already select a class");
        classId = TestDataReader.getValue("class-id");
        announcementTitle = TestDataReader.getValue("valid-announcement-title");
        announcementDesc = TestDataReader.getValue("valid-announcement-desc");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-009 - User can create class announcement")
    public void createBootcampAnnouncement() {
        // Payload / Test Data can be found at Test Steps 3
        Map<String, Object> input = new HashMap<>();
        input.put("bootcampId", classId);
        input.put("title", announcementTitle);
        input.put("content", "<p>"+announcementDesc+"</p>");

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "createBootcampAnnouncement", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.createBootcampAnnouncement"));
        Assert.assertTrue(jsonPath.get("data.createBootcampAnnouncement") instanceof Map);

        // Validate announcement props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.createBootcampAnnouncement");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        announcementId = (String)dataObj.get("id");

        logger.info("User can create class announcement: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created announcement
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("createBootcampAnnouncement")) {
            TestDataReader.setValue("created-announcement-title", announcementTitle);
            TestDataReader.setValue("created-announcement-id", announcementId);
        }
    }
}
