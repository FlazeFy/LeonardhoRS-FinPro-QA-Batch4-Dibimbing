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

import static core.TestUtil.templateGraphQLRequest;

public class MutationDeleteBootcampAnnouncementTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationUpdateBootcampAnnouncementTest.class);
    private String sid;
    private String announcementId;

    private static final String mutation = """
     mutation deleteBootcampAnnouncement($id: String!) {
       deleteBootcampAnnouncement(id: $id) {
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

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Announcement Id", "value", announcementId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P2
    @Test(priority = 4, groups = {"api-test"}, description = "TC-CLMG-013 - User can delete class announcement")
    public void deleteBootcampAnnouncement() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", announcementId);

        // Request
        Response response = templateGraphQLRequest("deleteBootcampAnnouncement", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid);
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.deleteBootcampAnnouncement"));
        Assert.assertTrue(jsonPath.get("data.deleteBootcampAnnouncement") instanceof Map);

        // Validate announcement props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.deleteBootcampAnnouncement");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        logger.info("User can delete class announcement: executed successfully");
    }
}
