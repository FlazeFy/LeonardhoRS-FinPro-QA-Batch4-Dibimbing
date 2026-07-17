package test.integration.announcement;

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

public class MutationCreateAnnouncementTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationCreateAnnouncementTest.class);

    private String sid;
    private String announcementId;

    // Test Data
    private String title;
    private String content;

    private static final String mutation = """
    mutation createAnnouncement($input: AnnouncementInput!) {
      createAnnouncement(input: $input) {
        id
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        title = TestDataReader.getValue("valid-announcement-title");
        content = TestDataReader.getValue("valid-announcement-desc");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Announcement Title", "value", title),
                Map.of("key", "Announcement Content", "value", content)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-ANCM-006 - User can add announcement to all employee with valid data")
    public void createAnnouncement() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("title", title);
        input.put("content", "<p>" + content + "</p>");
        input.put("isForAllEmployee", true);
        input.put("division", new ArrayList<>());

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "createAnnouncement", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.createAnnouncement"));
        Assert.assertTrue(jsonPath.get("data.createAnnouncement") instanceof Map);

        // Validate announcement props
        Map<String, Object> dataObj = jsonPath.getMap("data.createAnnouncement");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        announcementId = (String) dataObj.get("id");

        logger.info("User can add announcement to all employee with valid data: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created announcement
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("createAnnouncement")) {
            TestDataReader.setValue("create-announcement-id-api", announcementId);
            TestDataReader.setValue("create-announcement-title-api", title);
        }
    }
}
