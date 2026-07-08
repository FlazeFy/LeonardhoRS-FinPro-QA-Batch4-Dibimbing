package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
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
public class FilterClassByBatch extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private DashboardPage dashboardPage;
    private String classBatch;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : At least one class exists
        classBatch = TestDataReader.getValue("angkatan");
        Assert.assertNotNull(classBatch, "Class batch test data must exist");
        Assert.assertFalse(classBatch.trim().isEmpty(), "Class batch test data must not be empty");
    }

    // Positive Test | P2 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-005 - User can filter classes by batch")
    public void testUserCanFilterClassByBatch() {
        logger.info("TS-1: - On the Dashboard page, click the 'Class' menu in the left sidebar");
        dashboardPage = new DashboardPage(DriverManager.getDriver());
        dashboardPage.clickClassMenuButton();

        logger.info("TS-2: Locate the section titled 'Manage Class'");
        classPage = new ClassPage(DriverManager.getDriver());
        Assert.assertTrue(classPage.isClassManagementSectionTitleDisplayed(), "Section title 'Manage Class' must be visible");

        logger.info("TS-3: Locate the search input field with the placeholder 'Filter by Angkatan'");
        Assert.assertTrue(classPage.isFilterClassBatchDisplayed(), "Batch class filter must be visible");

        logger.info("TS-4: Select the batch");
        classPage.selectClassBatch(classBatch);

        logger.info("Expected Result: The system should return only classes that match the selected batch");

        Assert.assertTrue(classPage.isClassListDisplayed(), "Class list not displayed correctly");

        // Validate each fields not empty / not whitespace only
        List<Map<String, String>> classData = classPage.getClassCardData();
        for (Map<String, String> dt : classData) {
            String expectedClassBatch = classBatch.toLowerCase().trim();
            String actualClassBatch = dt.get("batch").toLowerCase().trim();

            // Validate the class batch
            Assert.assertTrue(actualClassBatch.contains(expectedClassBatch), "Expected "+expectedClassBatch+" to contain in "+actualClassBatch);
        }

        logger.info("User can filter classes by batch: executed successfully");
    }
}
