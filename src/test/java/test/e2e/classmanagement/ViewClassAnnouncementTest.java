package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.AllPage;
import org.example.ClassPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

// FR-ID    : FR-CLMG-05
// Module   : Class Management
public class ViewClassAnnouncementTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;
    private String announcementTitle;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");
        // Pre-Condition : At least one announcement exists
        announcementTitle = TestDataReader.getValue("created-announcement-title");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Announcement Title", "value", announcementTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P2 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-008 - User can view class announcement")
    public void testUserCanViewClassAnnouncement() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Announcement tab");
        classPage.openTabByTitle("Announcement");

        logger.info("TS-2: Scroll to the Create Announcement section");
        Assert.assertTrue(classPage.isClassAnnouncementSectionTitleDisplayed(), "Section title 'Announcement' must be visible");

        logger.info("TS-3: Observe the list of announcements displayed below the form");
        logger.info("Expected Result: The announcement list is displayed and contains all required information (title, description, author, created date, edit and delete button)");

        Assert.assertTrue(classPage.isAnnouncementListDisplayed(), "Announcement list not displayed correctly");

        // Validate each fields not empty / not whitespace only
        List<Map<String, String>> classData = classPage.getAnnouncementCardData();
        TestUtil.validateNotEmptyString(classData, null);

        boolean isFound = false;
        for (Map<String, String> dt : classData) {
            String actualTitle = dt.get("title");
            if (announcementTitle.equals(actualTitle)) isFound = true;

            // validate role
            String role = dt.get("role");
            Assert.assertEquals(role, "Admin", "Role must be equal to 'Admin', but got "+role);

            // validate created at has format "dd MMMM yyyy, HH:mm"
            String date = dt.get("created-at");
            Assert.assertTrue(TestUtil.isValidDateFormat(date, "dd MMMM yyyy, HH:mm"), "Start date does not match expected format 'd MMMM yyyy'");
        }

        Assert.assertTrue(isFound, "Last created announcement not found in the list");

        logger.info("User can view class announcement: executed successfully");
    }
}
