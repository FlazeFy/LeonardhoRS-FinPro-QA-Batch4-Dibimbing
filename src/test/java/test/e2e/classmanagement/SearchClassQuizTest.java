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
public class SearchClassQuizTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;
    private String invalidTestTitle;
    private String validTestTitle;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");
        invalidTestTitle = TestDataReader.getValue("invalid-test-title");
        validTestTitle = TestDataReader.getValue("created-test-title-api");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Invalid Test Title", "value", invalidTestTitle),
                Map.of("key", "Valid Test Title", "value", validTestTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Negative Test | P4 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-046 - User can view no data message when no class test found")
    public void testUserCanViewNoDataMessageWhenNoClassTestFound() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Test tab");
        classPage.openTabByTitle("Test");

        logger.info("TS-2: Scroll to the List Test section");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'List Test' must be visible");

        logger.info("TS-3: On the Add Test pop-up, Locate the field with placeholder 'Search test'");
        Assert.assertTrue(classPage.isSearchClassTestDisplayed(), "Add Class Test search must be visible");

        logger.info("TS-4: Type the keyword into the search field");
        classPage.fillSearchClassTest(invalidTestTitle);
        classPage.waitForPageLoading();

        logger.info("Expected Result: System show failed message 'Class test not found'");
        Assert.assertTrue(classPage.isClassTestFailedMessageDisplayed("Class test not found"), "The failed message must be visible and match 'No test found with this keyword'");

        logger.info("User can view no data message when no test found: executed successfully");
    }

    // Positive Test | P3 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-047 - User can search class test with valid keyword")
    public void testUserSearchClassTestWithValidKeyword() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Test tab");
        classPage.openTabByTitle("Test");

        logger.info("TS-2: Scroll to the List Test section");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'List Test' must be visible");

        logger.info("TS-3: On the Add Test pop-up, Locate the field with placeholder 'Search test'");
        Assert.assertTrue(classPage.isSearchClassTestDisplayed(), "Add Class Test search must be visible");

        logger.info("TS-4: Type the keyword into the search field");
        classPage.fillSearchClassTest(validTestTitle);
        classPage.waitForPageLoading();

        logger.info("Expected Result: The test list is displayed, contains all required information (title, start date, duration, test type, and action button), and test title related with search keyword");
        Assert.assertTrue(classPage.isTableDisplayed(classPage.getClassTestSectionTitle(), null), "Table must be visible");
        Assert.assertTrue(classPage.isTableDataValid(classPage.getClassTestSectionTitle(),null, "button"), "All table body data must not empty");

        // Validate table data
        List<Map<String, String>> testData = classPage.getTableData(classPage.getClassTestSectionTitle(), null);
        for (Map<String, String> dt: testData) {
            String startDate = dt.get("Start Date");
            // validate start date has format "d MMMM yyyy, HH:mm a"
            Assert.assertTrue(TestUtil.isValidDateFormat(startDate, "d MMMM yyyy, HH:mm a"), "Start date does not match expected format 'd MMMM yyyy'");

            String actualTestTitle = dt.get("Title");
            Assert.assertTrue(actualTestTitle.contains(validTestTitle), "Expected " + validTestTitle + " to contain in " + actualTestTitle);
        }

        logger.info("User can search class test with valid keyword: executed successfully");
    }
}
