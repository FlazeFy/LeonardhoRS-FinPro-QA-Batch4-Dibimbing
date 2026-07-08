package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ClassPage;
import org.example.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

// FR-ID    : FR-CLMG-01
// Module   : Class Management
public class SearchClassTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private DashboardPage dashboardPage;
    private String classTitle;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : At least one class exists
        classTitle = TestDataReader.getValue("class-title");
        Assert.assertNotNull(classTitle, "Class title test data must exist");
        Assert.assertFalse(classTitle.trim().isEmpty(), "Class title test data must not be empty");
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-003 - User can search classes with valid keyword")
    public void testUserCanSearchClassesWithValidKeyword() {
        logger.info("TS-1: - On the Dashboard page, click the 'Class' menu in the left sidebar");
        dashboardPage = new DashboardPage(DriverManager.getDriver());
        dashboardPage.clickClassMenuButton();

        logger.info("TS-2: Locate the section titled 'Manage Class'");
        classPage = new ClassPage(DriverManager.getDriver());
        Assert.assertTrue(classPage.isClassManagementSectionTitleDisplayed(), "Section title 'Manage Class' must be visible");

        logger.info("TS-3: Locate the search input field with the placeholder 'Search class...'");
        Assert.assertTrue(classPage.isSearchClassDisplayed(), "Search class input must be visible");

        logger.info("TS-4: Type the keyword into the search field");
        classPage.fillSearchClass(classTitle);

        logger.info("Expected Result: The system should return only classes that match the entered keyword");

        Assert.assertTrue(classPage.isClassListDisplayed(), "Class list not displayed correctly");

        // Validate each fields not empty / not whitespace only
        List<Map<String, String>> classData = classPage.getClassCardData();
        for (Map<String, String> dt : classData) {
            String expectedClassTitle = classTitle.toLowerCase().trim();
            String actualClassTitle = dt.get("title").toLowerCase().trim();

            // Validate the class title
            Assert.assertTrue(actualClassTitle.contains(expectedClassTitle), "Expected "+expectedClassTitle+" to contain in "+actualClassTitle);
        }

        logger.info("User can search classes with valid keyword: executed successfully");
    }
}
