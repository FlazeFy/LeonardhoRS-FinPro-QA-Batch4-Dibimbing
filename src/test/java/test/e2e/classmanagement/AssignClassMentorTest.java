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

// FR-ID    : FR-CLMG-11
// Module   : Class Management
public class AssignClassMentorTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;
    private String validMentorName;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");
        validMentorName = TestDataReader.getValue("valid-add-mentor-name");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Valid Mentor Name", "value", validMentorName)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P1 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-042 - User can assigne new class mentor")
    public void testUserCanAssigneNewClassMentor() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Mentor tab");
        classPage.openTabByTitle("Mentor");

        logger.info("TS-2: Click 'Add Mentor' button");
        classPage.openAddMentor();

        logger.info("TS-3: On the Add Mentor pop-up, Locate the field with placeholder 'Search name'");
        Assert.assertTrue(classPage.isAddClassMentorPopupDisplayed(), "Add Class Mentor pop-up must be visible");
        Assert.assertTrue(classPage.isSearchAddClassMentorDisplayed(), "Add Class Mentor search must be visible");

        logger.info("TS-4: Type the mentor name");
        classPage.fillSearchAddClassMentor(validMentorName);
        classPage.waitForPageLoading();

        logger.info("TS-5: Check the checkbox at the Choose column");
        String selectedMentor = classPage.selectFirstMentorAndGetName();
        Assert.assertFalse(selectedMentor.isEmpty(), "No valid mentor found");

        logger.info("TS-6: Click 'Add Mentor' button");
        classPage.assignAddMentor();

        logger.info("TS-7: On the 'Add Mentor' Validation Pop-Up, Click 'Add Mentor' button again");
        Assert.assertTrue(classPage.isSelectedMentorValid(selectedMentor), "Selected mentor must be listed in the confirmation add mentor list");
        classPage.confirmAddMentor();

        logger.info("Expected Result: The 'Add Mentor' pop-up disappears, and the new mentor is added to the list");
        Assert.assertTrue(classPage.isSearchAddClassMentorDisappear(), "Add mentor pop-up must be disappear");

        logger.info("User can assigne new class mentor: executed successfully");
    }
}
