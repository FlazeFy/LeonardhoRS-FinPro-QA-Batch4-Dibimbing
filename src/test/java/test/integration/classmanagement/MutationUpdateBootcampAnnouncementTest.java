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

public class MutationUpdateBootcampAnnouncementTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationUpdateBootcampAnnouncementTest.class);
    private String sid;
    private String announcementId;
    private String announcementTitle;
    private String announcementDesc;

    private static final String mutation = """
    mutation updateBootcampAnnouncement($input: InputBootcampAnnouncement!, $id: String!) {
      updateBootcampAnnouncement(input: $input, id: $id) {
        id
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: At least one announcement exists");
        announcementId = TestDataReader.getValue("created-announcement-id");
        announcementTitle = TestDataReader.getValue("valid-announcement-title");
        announcementDesc = TestDataReader.getValue("valid-announcement-desc");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Announcement Id", "value", announcementId),
                Map.of("key", "Announcement Title", "value", announcementTitle),
                Map.of("key", "Announcement Description", "value", announcementDesc)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P2 | Valid
    @Test(priority = 2, groups = {"api-test"}, description = "TC-CLMG-011 - User can update class announcement")
    public void updateBootcampAnnouncement() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("title", announcementTitle+"-edit");
        input.put("content", "<p>" + announcementDesc + "-edit</p>");
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", announcementId);
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "updateBootcampAnnouncement", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.updateBootcampAnnouncement"));
        Assert.assertTrue(jsonPath.get("data.updateBootcampAnnouncement") instanceof Map);

        // Validate announcement props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.updateBootcampAnnouncement");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        logger.info("User can update class announcement: executed successfully");
    }
}
