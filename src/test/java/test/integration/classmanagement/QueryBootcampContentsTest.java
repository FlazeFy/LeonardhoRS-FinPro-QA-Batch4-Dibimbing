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

public class QueryBootcampContentsTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompanyTest.class);
    private String sid;
    private String classId;

    // Test Data
    private String contentTitle;

    private static final String query = """
    query BootcampContents($bootcampId: String!, $param: InputBaseQuery!) {
      bootcampContents(bootcampId: $bootcampId, param: $param) {
        id title descriptions liveClassTime hideContent liveClassDuration
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
        contentTitle = TestDataReader.getValue("created-content-title");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId),
                Map.of("key", "Content Title", "value", contentTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P3
    @Test(priority = 2, groups = {"api-test"}, description = "TC-CLMG-021 - User can search class content with valid keyword")
    public void bootcampContents() {
        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("search", contentTitle);
        param.put("page", 1);
        param.put("limit", 5);

        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("param", param);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "bootcampContents", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.bootcampContents"));
        List<Map<String, Object>> contents = jsonPath.getList("data.bootcampContents");

        // Validate data types
        List<String> stringFields = List.of("id", "title", "descriptions", "liveClassTime");
        TestUtil.validateColumn(contents, stringFields, "string", false);

        List<String> boolFields = List.of("hideContent");
        TestUtil.validateColumn(contents, boolFields, "boolean", false);

        List<String> intNullableFields = List.of("liveClassDuration");
        TestUtil.validateColumn(contents, intNullableFields, "number", true);

        // Validate not empty
        List<String> notEmptyStringFields = List.of("id", "title", "descriptions", "liveClassTime");
        TestUtil.validateNotEmptyString(contents, notEmptyStringFields);

        for (Map<String, Object> dt: contents) {
            String expectedContentTitle = contentTitle.toLowerCase();
            String actualContentTitle = ((String)dt.get("title")).toLowerCase();

            // Make sure content title related to search keyword
            Assert.assertTrue(actualContentTitle.contains(expectedContentTitle), "Expected "+expectedContentTitle+" to contain in "+actualContentTitle);
        }

        logger.info("User can search class content with valid keyword: executed successfully");
    }
}
