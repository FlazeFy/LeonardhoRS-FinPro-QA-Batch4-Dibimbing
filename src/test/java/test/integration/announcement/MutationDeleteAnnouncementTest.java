package test.integration.announcement;

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

public class MutationDeleteAnnouncementTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationDeleteAnnouncementTest.class);
    private String sid;
    private String announcementId;

    private static final String mutation = """
     mutation deleteAnnouncement($id: String!) {
       deleteAnnouncement(id: $id)
     }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: At least one announcement exists");
        announcementId = TestDataReader.getValue("create-announcement-id-api");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Announcement Id", "value", announcementId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P2
    @Test(priority = 4, groups = {"api-test"}, description = "TC-ANCM-005 - User can delete announcement")
    public void deleteAnnouncement() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", announcementId);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "deleteAnnouncement", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.deleteAnnouncement"));
        Assert.assertTrue(jsonPath.getBoolean("data.deleteAnnouncement"));

        logger.info("User can delete announcement: executed successfully");
    }
}
