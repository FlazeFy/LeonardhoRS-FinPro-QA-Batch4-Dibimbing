package test.integration.announcement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.TestDataReader;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import core.BaseApiTest;
import core.TestUtil;

public class QueryAnnouncementsTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryAnnouncementsTest.class);

    private String sid;

    // Test Data
    private String announcementTitle;

    private static final String query = """
        query announcements(
          $orderBy: String, $orderColumn: String, $page: Float, $limit: Float, $divisionIds: [String!], $programIds: [String!], $search: String
        ) {
          announcements(
            orderBy: $orderBy, orderColumn: $orderColumn, page: $page, limit: $limit, divisionIds: $divisionIds, programIds: $programIds, search: $search
          ) {
            id title content createdAt updatedAt linkUrl isForAllEmployee bootcampId
            division { id title }
          }
        }
        """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: At least one announcement exists");
        announcementTitle = TestDataReader.getValue("create-announcement-title-api");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Announcement Title", "value", announcementTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    private void validateAnnouncements(List<Map<String, Object>> announcements) {
        // Validate announcement props
        List<String> stringFields = List.of("id", "createdAt", "updatedAt");
        TestUtil.validateColumn(announcements, stringFields, "string", false);

        List<String> stringNullableFields = List.of("title", "content", "linkUrl", "bootcampId");
        TestUtil.validateColumn(announcements, stringNullableFields, "string", true);

        List<String> booleanFields = List.of("isForAllEmployee");
        TestUtil.validateColumn(announcements, booleanFields, "boolean", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id", "createdAt", "updatedAt");
        TestUtil.validateNotEmptyString(announcements, notEmptyStringFields);

        // Validate division
        for (Map<String, Object> dt : announcements) {
            Object divisionObj = dt.get("division");
            if (divisionObj == null) continue;

            List<Map<String, Object>> divisions = (List<Map<String, Object>>) divisionObj;

            TestUtil.validateColumn(divisions, List.of("id"), "string", false);
            TestUtil.validateColumn(divisions, List.of("title"), "string", true);
            TestUtil.validateNotEmptyString(divisions, List.of("id"));
        }
    }

    // Positive Test | P2
    @Test(priority = 1, groups = {"api-test"}, description = "TC-ANCM-001 - User can view announcement")
    public void announcements() {
        // Params
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderBy", "DESC");
        variables.put("orderColumn", "createdAt");
        variables.put("page", 1);
        variables.put("limit", 10);
        variables.put("search", "");

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "announcements", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.announcements"));
        List<Map<String, Object>> announcements = jsonPath.getList("data.announcements");

        // Main validation
        validateAnnouncements(announcements);

        logger.info("User can view announcement: executed successfully");
    }

    // Positive Test | P3
    @Test(priority = 1, groups = {"api-test"}, description = "TC-ANCM-003 - User can search announcement with valid keyword")
    public void announcementsWithValidKeyword() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Params
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderBy", "DESC");
        variables.put("orderColumn", "createdAt");
        variables.put("page", 1);
        variables.put("limit", 10);
        variables.put("search", announcementTitle);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "announcements", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.announcements"));
        List<Map<String, Object>> announcements = jsonPath.getList("data.announcements");

        // Main validation
        validateAnnouncements(announcements);

        // Validate announcement title
        for (Map<String, Object> dt : announcements) {
            Object actualTitleObj = dt.get("title");
            String actualTitle = (String)actualTitleObj;
            Assert.assertTrue(actualTitle.contains(announcementTitle), "Announcement title must be related with search keyword");
        }

        logger.info("User can search announcement with valid keyword: executed successfully");
    }
}
