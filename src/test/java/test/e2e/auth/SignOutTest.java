package test.e2e.auth;

import core.BaseTest;
import core.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.DashboardPage;
import org.example.page.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

// FR-ID    : FR-AUTH-02
// Module   : Auth
public class SignOutTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(LoginTest.class);
    private DashboardPage dashboardPage;

    // Positive Test | P1
    @Test(priority = 3, groups = {"ui-test"}, description = "TC-AUTH-008 - User can sign out from the app")
    public void testUserCanSignOutFromTheApp() {
        logger.info("Pre-Condition: User already signed in");
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.doLogin(config.getProperty("validEmailAuth"), config.getProperty("validPasswordAuth"));

        // Test Steps
        logger.info("TS-1: On the Dashboard page, click the 'Profile' button in the navbar");
        dashboardPage = new DashboardPage(DriverManager.getDriver());
        dashboardPage.clickProfileButton();

        logger.info("TS-2: Click the Sign Out button");
        dashboardPage.clickSignOutButton();

        // Expected Result
        logger.info("Expected Result: System redirect to Login page");
        loginPage.waitForUrlToContain("/login");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"), "Expected to stay at the same page (Login), but URL was: " + currentUrl);

        logger.info("User can sign out from the app : executed successfully");
    }
}
