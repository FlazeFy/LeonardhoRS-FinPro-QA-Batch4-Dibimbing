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

import java.util.List;
import java.util.Map;

import static core.TestUtil.templateGraphQLRequest;

public class QueryAngkatan extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompany.class);

    private static final String query = """
        query Angkatans {
          angkatans {
            id name
          }
        }
        """;

    // Positive Test | P2 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-005 - User can filter classes by batch")
    public void angkatans() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Request
        Response response = templateGraphQLRequest("angkatans", query, null, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid);
        JsonPath jsonPath = response.jsonPath();

        System.out.println(jsonPath);

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.angkatans"));
        List<Map<String, Object>> angkatans = jsonPath.getList("data.angkatans");

        // Validate angkatans props
        // Validate each fields data type
        List<String> stringFields = List.of("name");
        TestUtil.validateColumn(angkatans, stringFields, "string", false);

        List<String> intFields = List.of("id");
        TestUtil.validateColumn(angkatans, intFields, "number", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("name");
        TestUtil.validateNotEmptyString(angkatans, notEmptyStringFields);

        logger.info("User can filter classes by batch: executed successfully");
    }
}
