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

public class QueryBootcampAnnouncementsTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompanyTest.class);
    private String sid;
    private String classId;

    private static final String query = """
    query BootcampAnnouncements($bootcampId: String!, $param: InputBaseQuery!) {
      bootcampAnnouncements(bootcampId: $bootcampId, param: $param) {
        id title content createdAt
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: User already select a class");
        classId = TestDataReader.getValue("created-bootcamp-id-api");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1
    @Test(priority = 2, groups = {"api-test"}, description = "TC-CLMG-008 - User can view class announcement")
    public void bootcampAnnouncements() {
        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("orderColumn", "createdAt");

        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("param", param);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "bootcampAnnouncements", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.bootcampAnnouncements"));
        List<Map<String, Object>> announcements = jsonPath.getList("data.bootcampAnnouncements");

        // Validate data types
        List<String> stringFields = List.of("id", "title", "content", "createdAt");
        TestUtil.validateColumn(announcements, stringFields, "string", false);

        List<String> stringNullableFields = List.of("title", "content");
        TestUtil.validateColumn(announcements, stringNullableFields, "string", true);

        // Validate not empty
        List<String> notEmptyStringFields = List.of("id", "createdAt");
        TestUtil.validateNotEmptyString(announcements, notEmptyStringFields);

        logger.info("User can view class announcement: executed successfully");
    }
}
