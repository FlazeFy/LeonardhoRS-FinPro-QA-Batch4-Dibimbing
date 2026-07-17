package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.ClassPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

// FR-ID    : FR-CLMG-06
// Module   : Class Management
public class ViewClassQuizTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P1
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-045 - User can view class test")
    public void testUserCanViewClassTest() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Test tab");
        classPage.openTabByTitle("Test");

        logger.info("TS-2: Scroll to the List Test section");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'List Test' must be visible");

        logger.info("TS-3: Observe the list of test displayed in table.");
        logger.info("Expected Result: The test list is displayed and contains all required information (title, start date, duration, test type, and action button)");
        Assert.assertTrue(classPage.isTableDisplayed(classPage.getClassTestSectionTitle(), null), "Table must be visible");
        Assert.assertTrue(classPage.isTableDataValid(classPage.getClassTestSectionTitle(),null, "button"), "All table body data must not empty");

        // Validate table data
        List<Map<String, String>> testData = classPage.getTableData(classPage.getClassTestSectionTitle(), null);
        for (Map<String, String> dt: testData) {
            String startDate = dt.get("Start Date");
            // validate start date has format "d MMMM yyyy, HH:mm a"
            Assert.assertTrue(TestUtil.isValidDateFormat(startDate, "d MMMM yyyy, HH:mm a"), "Start date does not match expected format 'd MMMM yyyy'");
        }

        logger.info("User can view class test: executed successfully");
    }
}
