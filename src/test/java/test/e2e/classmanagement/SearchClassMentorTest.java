package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ClassPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

// FR-ID    : FR-CLMG-11
// Module   : Class Management
public class SearchClassMentorTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;
    private String invalidMentorName;
    private String validMentorName;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");
        invalidMentorName = TestDataReader.getValue("invalid-mentor-name");
        validMentorName = TestDataReader.getValue("valid-mentor-name");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Invalid Mentor Name", "value", invalidMentorName),
                Map.of("key", "Valid Mentor Name", "value", validMentorName)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Negative Test | P4 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-038 - User can view no data message when no mentor found")
    public void testUserCanViewNoDataMessageWhenNoClassMentorFound() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Mentor tab");
        classPage.openTabByTitle("Mentor");

        logger.info("TS-2: Click 'Add Mentor' button");
        classPage.openAddMentor();

        logger.info("TS-3: On the Add Mentor pop-up, Locate the field with placeholder 'Search name'");
        Assert.assertTrue(classPage.isAddClassMentorPopupDisplayed(), "Add Class Mentor pop-up must be visible");
        Assert.assertTrue(classPage.isSearchAddClassMentorDisplayed(), "Add Class Mentor search must be visible");

        logger.info("TS-4: Type the mentor name");
        classPage.fillSearchAddClassMentor(invalidMentorName);
        classPage.waitForPageLoading();

        logger.info("Expected Result: System show failed message 'No employee found with this keyword'");
        Assert.assertTrue(classPage.isClassAddMentorFailedMessageDisplayed("No employee found with this keyword"), "The failed message must be visible and match 'No mentor found with this keyword'");

        logger.info("User can view no data message when no mentor found: executed successfully");
    }
}
