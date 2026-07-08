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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

// FR-ID    : FR-CLMG-01
// Module   : Class Management
public class ViewClassTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private DashboardPage dashboardPage;

    // Pre-Condition : User already signed in
    @BeforeMethod
    public void setUp() {
        loginAsValidUser();
    }

    // Positive Test | P1
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-001 - User can view all classes")
    public void testUserCanViewAllClasses() {
        logger.info("TS-1: On the Dashboard page, click the 'Class' menu in the left sidebar\n");
        dashboardPage = new DashboardPage(DriverManager.getDriver());
        dashboardPage.clickClassMenuButton();

        logger.info("TS-2: Locate the section titled 'Manage Class'");
        classPage = new ClassPage(DriverManager.getDriver());
        Assert.assertTrue(classPage.isClassManagementSectionTitleDisplayed(), "Section title 'Manage Class' must be visible");

        logger.info("TS-3: Observe the list of class displayed");
        logger.info("Expected Result: The class list is displayed and contains all required information " +
                "(image, name, description, major, batch, total employees, date created, edit button, and delete button)");

        Assert.assertTrue(classPage.isClassListDisplayed(), "Class list not displayed correctly");

        // Validate each fields not empty / not whitespace only
        List<Map<String, String>> classData = classPage.getClassCardData();
        TestUtil.validateNotEmptyString(classData, null);

        for (Map<String, String> dt : classData) {
            // validate total employee is a valid non-negative number
            String totalEmployeeAssigned = dt.get("employee");
            Assert.assertTrue(TestUtil.isValidPositiveNumber(totalEmployeeAssigned), "Total assigned employee should be at least 0, but got "+totalEmployeeAssigned);

            // validate start date has format "d MMMM yyyy"
            String date = dt.get("startDate");
            Assert.assertTrue(TestUtil.isValidDateFormat(date, "d MMMM yyyy"), "Start date does not match expected format 'd MMMM yyyy'");
        }

        logger.info("User can view all classes: executed successfully");
    }

    @AfterMethod
    public void tearDown() {
        // Store first class title for next test scenario
        if (classPage != null) {
            List<Map<String, String>> classData = classPage.getClassCardData();

            if (!classData.isEmpty()) {
                String classTitle = classData.get(0).get("title");
                if (classTitle != null && !classTitle.trim().isEmpty()) TestDataReader.setValue("class-title", classTitle);
            }
        }
    }
}
