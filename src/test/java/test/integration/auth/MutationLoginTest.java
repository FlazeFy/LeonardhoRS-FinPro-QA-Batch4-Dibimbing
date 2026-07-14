package test.integration.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.e2e.auth.LoginTest;
import core.BaseApiTest;
import core.TestUtil;

import static core.TestUtil.templateGraphQLRequest;

public class MutationLoginTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(LoginTest.class);

    private static final String mutation = """
            mutation Login(
               $companyId: String!
               $usernameOrEmail: String!
               $password: String!
             ) {
               login(
                 companyId: $companyId
                 usernameOrEmail: $usernameOrEmail
                 password: $password
               ) {
                 user {
                   id name username email role
                 }
                 errors {
                   field message
                 }
               }
             }
            """;

    // Positive Test | P1 | Valid Case
    @Test(priority = 2, groups = {"api-test"}, description = "TC-AUTH-001 - User can login into the app using valid basic auth")
    public void userCanLoginWithValidData() {
        logger.info("Pre-Condition: Admin account already registered");

        // Payload / Test Data can be found at Test Steps 2
        Map<String, Object> variables = new HashMap<>();
        variables.put("usernameOrEmail", config.getProperty("validEmailAuth"));
        variables.put("password", config.getProperty("validPasswordAuth"));
        variables.put("companyId", config.getProperty("companyId"));

        // Request
        Response response = templateGraphQLRequest("login", mutation, variables, null, null, null);
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.login"));
        Assert.assertNotNull(jsonPath.get("data.login.user"));
        Assert.assertNull(jsonPath.get("data.login.errors"));
        Assert.assertTrue(jsonPath.get("data.login.user") instanceof Map);

        // Validate user props
        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("data.login.user");
        List<String> stringFields = List.of("id", "username", "email", "role");
        List<String> stringNullableFields = List.of("name");

        TestUtil.validateColumn(dataObj, stringFields, "string", false);
        TestUtil.validateColumn(dataObj, stringNullableFields, "string", true);

        logger.info("User can login into the app using valid basic auth : executed successfully");
    }

    // Negative Test | P1 | Invalid Case
    @Test(priority = 1, groups = {"api-test"}, description = "TC-AUTH-002 - User cant login into the app using invalid wrong password")
    public void userCantLoginWithWrongPassword() {
        logger.info("Pre-Condition: Admin account already registered");

        // Payload / Test Data can be found at Test Steps 2
        Map<String, Object> variables = new HashMap<>();
        variables.put("usernameOrEmail", config.getProperty("validEmailAuth"));
        variables.put("password", config.getProperty("invalidPasswordAuth"));
        variables.put("companyId", config.getProperty("companyId"));

        // Request
        Response response = templateGraphQLRequest("login", mutation, variables, null, null, null);
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.login"));
        Assert.assertNull(jsonPath.get("data.login.user"));

        // Errors should exist
        Assert.assertNotNull(jsonPath.get("data.login.errors"));
        List<Map<String, Object>> errors = jsonPath.getList("data.login.errors");
        Assert.assertEquals(errors.size(), 1);
        Assert.assertEquals(errors.get(0).get("field"), "usernameOrEmail");

        // Expected Result
        logger.info("Expected Result: System show failed message 'wrong username or password'");
        Assert.assertEquals(errors.get(0).get("message"), "wrong username or password");

        logger.info("User cant login into the app using invalid wrong password : executed successfully");
    }
}