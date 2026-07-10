package test.integration.dashboard;

import core.BaseApiTest;
import core.TestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import static core.TestUtil.templateGraphQLRequest;

public class QueryCountEmployeesTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompanyTest.class);

    private static final String query = """
          query CountEmployees {
            countEmployees
          }
        """;

    // Positive Test | P4
    @Test(priority = 1, groups = {"api-test"}, description = "TC-DASH-001 - User can view company profile")
    public void countEmployeesValidData() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Request
        Response response = templateGraphQLRequest("countEmployees", query, null, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid);
        JsonPath jsonPath = response.jsonPath();

        // Validate total employee
        Object countEmployees = jsonPath.get("data.countEmployees");
        Assert.assertNotNull(countEmployees);
        Assert.assertTrue(countEmployees instanceof Integer, "countEmployees should be an Integer");

        int count = (int)countEmployees;
        Assert.assertTrue(count >= 0,  "countEmployees should be greater than or equal to 0");

        logger.info("User can view company profile: executed successfully");
    }
}
