package test.integration.classmanagement;

import core.BaseApiTest;
import core.TestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.integration.dashboard.QueryMyCompany;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.TestUtil.templateGraphQLRequest;

public class QueryBootcamps extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompany.class);

    private static final String query = """
        query Bootcamps($param: InputBaseQuery!) {
          bootcamps(param: $param) {
            id title descriptions angkatanId divisionId countAssignedUser startedAt imageMedia { url }
          }
        }
        """;

    // Positive Test | P1
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-001 - User can view all classes")
    public void bootcamps() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("search", "");
        param.put("orderColumn", "createdAt");
        param.put("orderBy", "DESC");
        Map<String, Object> variables = new HashMap<>();
        variables.put("param", param);

        // Request
        Response response = templateGraphQLRequest("bootcamps", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid);
        JsonPath jsonPath = response.jsonPath();

        System.out.println(jsonPath);

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.bootcamps"));
        List<Map<String, Object>> bootcamps = jsonPath.getList("data.bootcamps");

        // Validate bootcamps props
        // Validate each fields data type
        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(bootcamps, stringFields, "string", false);

        List<String> stringNullableFields = List.of("title", "descriptions", "startedAt");
        TestUtil.validateColumn(bootcamps, stringNullableFields, "string", true);

        List<String> intFields = List.of("countAssignedUser");
        TestUtil.validateColumn(bootcamps, intFields, "number", false);

        List<String> intNullableFields = List.of("angkatanId", "divisionId");
        TestUtil.validateColumn(bootcamps, intNullableFields, "number", true);

        // Validate each fields not empty / not whitespace only
        // check this again .....
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(bootcamps, notEmptyStringFields);

        // Validate imageMedia.url
        for (Map<String, Object> bootcamp: bootcamps) {
            Object imageMediaObj = bootcamp.get("imageMedia");
            if (imageMediaObj == null) continue;

            Map<String, Object> imageMedia = (Map<String, Object>) imageMediaObj;
            Assert.assertTrue(imageMedia.containsKey("url"), "imageMedia must contain 'url'");

            Object urlObj = imageMedia.get("url");
            Assert.assertTrue(urlObj instanceof String, "url must be a string");
            Assert.assertFalse(((String) urlObj).trim().isEmpty(), "url must not be empty");
        }

        logger.info("User can view all classes: executed successfully");
    }
}
