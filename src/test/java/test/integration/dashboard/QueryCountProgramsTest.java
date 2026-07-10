package test.integration.dashboard;

import core.BaseApiTest;
import core.TestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QueryCountProgramsTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompanyTest.class);

    private static final String query = """
          query CountPrograms {
            countPrograms
          }
        """;

    // Positive Test | P3
    @Test(priority = 1, groups = {"api-test"}, description = "TC-DASH-002 - User can view fast track summary")
    public void countProgramsValidData() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "countPrograms", query, null, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate total employee
        Object countPrograms = jsonPath.get("data.countPrograms");
        Assert.assertNotNull(countPrograms);
        Assert.assertTrue(countPrograms instanceof Integer, "countPrograms should be an Integer");

        int count = (int)countPrograms;
        Assert.assertTrue(count >= 0,  "countPrograms should be greater than or equal to 0");

        logger.info("User can view fast track summary: executed successfully");
    }
}
