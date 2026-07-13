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

public class MutationToggleHideBootcampContentTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationToggleHideBootcampContentTest.class);
    private String sid;
    private String contentId;

    private static final String mutation = """
     mutation toggleHideBootcampContent($id: String!) {
       toggleHideBootcampContent(id: $id) {
         id
       }
     }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: At least one content exists");
        contentId = TestDataReader.getValue("created-content-id");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Content Id", "value", contentId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P2
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-027 - User can hide class content")
    public void toggleHideBootcampContent() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", contentId);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "toggleHideBootcampContent", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.toggleHideBootcampContent"));
        Assert.assertTrue(jsonPath.get("data.toggleHideBootcampContent") instanceof Map);

        // Validate content props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.toggleHideBootcampContent");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        logger.info("User can hide class content: executed successfully");
    }
}
