package test.integration.auth;

import core.BaseApiTest;
import core.TestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import static core.TestUtil.templateGraphQLRequest;

public class MutationLogoutTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationLogoutTest.class);

    private static final String mutation = """
        mutation Logout {
          logout
        }
        """;

    // Positive Test | P1
    @Test(priority = 1, groups = {"api-test"}, description = "TC-AUTH-008 - User can sign out from the app")
    public void userCanLogoutSuccessfully() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Request
        Response response = templateGraphQLRequest("logout", mutation, null, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid);
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.logout"));
        Object logout = jsonPath.get("data.logout");
        Assert.assertTrue(logout instanceof Boolean, "logout should be a Boolean");
        Assert.assertTrue((Boolean) logout);

        logger.info("User can sign out from the app: executed successfully");
    }
}
