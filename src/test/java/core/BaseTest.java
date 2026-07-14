package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.ClassPage;
import org.example.page.DashboardPage;
import org.example.page.LoginPage;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import java.util.Properties;

public class BaseTest {
    protected static Properties config;
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void loadConfig() {
        String env = System.getProperty("env");
        env = (env == null || env.isEmpty()) ? "staging" : env;
        config = ConfigReader.loadProperties(env);
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        DriverManager.initDriver(browser);
        DriverManager.getDriver().manage().window().maximize();
        DriverManager.getDriver().get(config.getProperty("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }

    // Pre-Condition
    protected void loginAsValidUser() {
        logger.info("Pre-Condition: User already signed in");
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.doLogin(config.getProperty("validEmailAuth"), config.getProperty("validPasswordAuth"));
        loginPage.waitForUrlToContain("/dashboard");

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/dashboard"),
                "Pre-condition failed: expected to land on dashboard page, but URL was: " + currentUrl);
    }

    protected void selectAClassByClassTitle(String classTitle) {
        DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
        dashboardPage.clickClassMenuButton();

        logger.info("Pre-Condition: User already select a class");
        ClassPage classPage = new ClassPage(DriverManager.getDriver());
        Assert.assertTrue(classPage.isClassManagementSectionTitleDisplayed(), "Section title 'Manage Class' must be visible");
        Assert.assertTrue(classPage.isSearchClassDisplayed(), "Search class input must be visible");

        classPage.fillSearchClass(classTitle);
        classPage.openClassDetail(classTitle);
        logger.info("Current Selected Class : " + classTitle);
    }
}