package test.e2e.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import core.BaseTest;
import core.DriverManager;

// FR-ID    : FR-AUTH-01
// Module   : Auth
public class LoginTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(LoginTest.class);
    private LoginPage loginPage;

    // Positive Test | P1 | Valid Case
    @Test(priority = 2, groups = {"ui-test"}, description = "TC-AUTH-001 - User can login into the app using valid basic auth")
    public void testUserCanLoginIntoTheAppUsingValidBasicAuth() {
        logger.info("Pre-Condition: Admin account already registered");

        // Test Steps
        logger.info("TS-1: Open the login page");
        loginPage = new LoginPage(DriverManager.getDriver());

        logger.info("TS-2: Fill the E-mail and Password field");
        String email = config.getProperty("validEmailAuth");
        String password = config.getProperty("validPasswordAuth");
        loginPage.fillLoginCredentials(email, password);

        logger.info("TS-3: Click the Sign In button");
        loginPage.clickSignIn();

        // Expected Result
        logger.info("Expected Result: System redirect to Dashboard page");
        loginPage.waitForUrlToContain("/dashboard");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        logger.info("Actual Result: Current URL is {}", currentUrl);

        Assert.assertTrue(currentUrl.contains("/dashboard"), "Expected redirect to Dashboard page, but URL was: " + currentUrl);

        logger.info("User can login into the app using valid basic auth : executed successfully");
    }

    // Negative Test | P1 | Invalid Case
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-AUTH-002 - User cant login into the app using invalid wrong password")
    public void testUserCantLoginIntoTheAppUsingInvalidWrongPassword() {
        logger.info("Pre-Condition: Admin account already registered");

        // Test Steps
        logger.info("TS-1: Open the login page");
        loginPage = new LoginPage(DriverManager.getDriver());

        logger.info("TS-2: Fill the E-mail and Password field");
        String email = config.getProperty("validEmailAuth");
        String password = config.getProperty("invalidPasswordAuth");
        loginPage.fillLoginCredentials(email, password);

        logger.info("TS-3: Click the Sign In button");
        loginPage.clickSignIn();

        // Expected Result
        logger.info("Expected Result: System show failed message 'wrong username or password'");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"), "Expected to stay at the same page (Login), but URL was: " + currentUrl);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
        Assert.assertEquals(loginPage.getErrorMessage(), "wrong username or password", "The error message is mismatched");

        logger.info("User cant login into the app using invalid wrong password : executed successfully");
    }

    // Negative Test | P2 | Edge Case
    // Notes : The manual TC still failed (has a bug)
    @Test(priority = 1, enabled = false, groups = {"ui-test"}, description = "TC-AUTH-003 - User cant login into the app using invalid character long")
    public void testUserCantLoginIntoTheAppUsingInvalidCharacterLong() {
        logger.info("Pre-Condition: Admin account already registered");

        // Test Steps
        logger.info("TS-1: Open the login page");
        loginPage = new LoginPage(DriverManager.getDriver());

        logger.info("TS-2: Fill the E-mail and Password field");
        String email = config.getProperty("validEmailAuth");
        String password = config.getProperty("invalidPasswordAuth");
        loginPage.fillLoginCredentials(email, password);

        logger.info("TS-3: Click the Sign In button");
        loginPage.clickSignIn();

        // Expected Result
        logger.info("Expected Result: System show failed message 'password must be at least 6 characters long'");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"), "Expected to stay at the same page (Login), but URL was: " + currentUrl);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
        Assert.assertEquals(loginPage.getErrorMessage(), "password must be at least 6 characters long", "The error message is mismatched");

        logger.info("User cant login into the app using invalid character long : executed successfully");
    }

    // Negative Test | P1 | Edge Case
    // Notes : The manual TC still failed (has a bug)
    @Test(priority = 1, enabled = false, groups = {"ui-test"}, description = "TC-AUTH-004 - User cant login into the app using empty field")
    public void testUserCantLoginIntoTheAppUsingEmptyField() {
        logger.info("Pre-Condition: Admin account already registered");

        // Test Steps
        logger.info("TS-1: Open the login page");
        loginPage = new LoginPage(DriverManager.getDriver());

        logger.info("TS-2: Fill the E-mail and Password field");
        String email = config.getProperty("validEmailAuth");
        String password = " ";
        loginPage.fillLoginCredentials(email, password);

        logger.info("TS-3: Click the Sign In button");
        loginPage.clickSignIn();

        // Expected Result
        logger.info("Expected Result: System show failed message 'password is required'");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"), "Expected to stay at the same page (Login), but URL was: " + currentUrl);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
        Assert.assertEquals(loginPage.getErrorMessage(), "password is required", "The error message is mismatched");

        logger.info("User cant login into the app using empty field : executed successfully");
    }

    // Negative Test | P2 | Invalid Case
    // Notes : The manual TC still failed (has a bug)
    @Test(priority = 1, enabled = false, groups = {"ui-test"}, description = "TC-AUTH-005 - User cant login into the app using invalid email")
    public void testUserCantLoginIntoTheAppUsingInvalidEmail() {
        logger.info("Pre-Condition: Admin account already registered");

        // Test Steps
        logger.info("TS-1: Open the login page");
        loginPage = new LoginPage(DriverManager.getDriver());

        logger.info("TS-2: Fill the E-mail and Password field");
        String email = config.getProperty("invalidEmailAuth");
        String password = config.getProperty("validPasswordAuth");
        loginPage.fillLoginCredentials(email, password);

        logger.info("TS-3: Click the Sign In button");
        loginPage.clickSignIn();

        // Expected Result
        logger.info("Expected Result: System show failed message 'email is invalid'");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"), "Expected to stay at the same page (Login), but URL was: " + currentUrl);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
        Assert.assertEquals(loginPage.getErrorMessage(), "email is invalid", "The error message is mismatched");

        logger.info("User cant login into the app using invalid email : executed successfully");
    }

    // Negative Test | P1 | Invalid Case
    // Notes : The manual TC still failed (has a bug)
    @Test(priority = 1, enabled = false, groups = {"ui-test"}, description = "TC-AUTH-006 - User cant login into the app using unregistered account")
    public void testUserCantLoginIntoTheAppUsingUnregisteredAccount() {
        logger.info("Pre-Condition: Admin account already registered");

        // Test Steps
        logger.info("TS-1: Open the login page");
        loginPage = new LoginPage(DriverManager.getDriver());

        logger.info("TS-2: Fill the E-mail and Password field");
        String email = config.getProperty("invalidUnregisteredEmailAuth");
        String password = config.getProperty("validPasswordAuth");
        loginPage.fillLoginCredentials(email, password);

        logger.info("TS-3: Click the Sign In button");
        loginPage.clickSignIn();

        // Expected Result
        logger.info("Expected Result: System show failed message 'account not found'");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"), "Expected to stay at the same page (Login), but URL was: " + currentUrl);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
        Assert.assertEquals(loginPage.getErrorMessage(), "account not found", "The error message is mismatched");

        logger.info("User cant login into the app using unregistered account : executed successfully");
    }

    // Positive Test | P4
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-AUTH-007 - User can masked their password")
    public void testUserCanMaskedTheirPassword() {
        logger.info("Pre-Condition: Admin account already registered");

        // Test Steps
        logger.info("TS-1: Open the login page");
        loginPage = new LoginPage(DriverManager.getDriver());

        logger.info("TS-2: Fill the E-mail and Password field");
        String email = config.getProperty("validEmailAuth");
        String password = config.getProperty("invalidPasswordAuth");
        loginPage.fillLoginCredentials(email, password);

        logger.info("TS-3: Toggle the Eye icon at the Password field");
        Assert.assertTrue(loginPage.isPasswordMasked(), "The password is not masked");
        loginPage.clickMaskedPassword();

        // Expected Result
        logger.info("Expected Result: Password can be masked or viewed");
        Assert.assertFalse(loginPage.isPasswordMasked(), "The password is masked");

        logger.info("User can masked their password : executed successfully");
    }
}
