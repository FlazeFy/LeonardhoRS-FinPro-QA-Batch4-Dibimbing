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
public class SearchAssignedClassMentorTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;
    private String invalidMentorName;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");
        invalidMentorName = TestDataReader.getValue("invalid-mentor-name");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Invalid Mentor Name", "value", invalidMentorName)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Negative Test | P2 | Invalid
    @Test(priority = 2, groups = {"ui-test"}, description = "TC-CLMG-035 - User can view no data message when no assigned class mentor found")
    public void testUserCanViewNoDataMessageWhenNoAssignedClassMentorFound() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Mentor tab");
        classPage.openTabByTitle("Mentor");

        logger.info("TS-2: Scroll to the Mentor List section");
        Assert.assertTrue(classPage.isClassMentorSectionTitleDisplayed(), "Section title 'Announcement' must be visible");

        logger.info("TS-3: Locate the field with placeholder 'Search name'");
        Assert.assertTrue(classPage.isSearchClassMentorDisplayed(), "Search class input must be visible");

        logger.info("TS-4: Type the mentor name");
        classPage.fillSearchClassMentor(invalidMentorName);
        classPage.waitForPageLoading();

        logger.info("Expected Result: System show failed message 'No mentor found with this keyword'");
        Assert.assertTrue(classPage.isClassMentorFailedMessageDisplayed("No mentor found with this keyword"), "The failed message must be visible and match 'No mentor found with this keyword'");

        logger.info("User can view no data message when no assigned class mentor found: executed successfully");
    }
}
