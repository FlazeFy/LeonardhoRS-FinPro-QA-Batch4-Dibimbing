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
    private String invalidTestName;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");
        invalidTestName = TestDataReader.getValue("invalid-test-name");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Invalid Test Name", "value", invalidTestName)
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
        classPage.fillSearchClassTest(invalidTestName);
        classPage.waitForPageLoading();

        logger.info("Expected Result: System show failed message 'Class test not found'");
        Assert.assertTrue(classPage.isClassTestFailedMessageDisplayed("Class test not found"), "The failed message must be visible and match 'No test found with this keyword'");

        logger.info("User can view no data message when no test found: executed successfully");
    }
}
