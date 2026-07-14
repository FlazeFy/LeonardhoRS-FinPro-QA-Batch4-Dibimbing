package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.AllPage;
import org.example.page.ClassPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

public class DeleteAnnouncementTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private AllPage allPage;

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
    @Test(priority = 2, groups = {"ui-test"}, description = "TC-CLMG-013 - User can delete class announcement")
    public void testUserCanDeleteClassAnnouncement() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Announcement tab");
        classPage.openTabByTitle("Announcement");

        logger.info("TS-2: Below the Create Announcement form. Select an announcement");
        classPage.findAnnouncementByTitle(announcementTitle);

        logger.info("TS-3: Click the 'Delete' button");
        classPage.clickDeleteAnnouncementByTitle(announcementTitle);

        logger.info("TS-4: On the Delete Announcement pop-up, click 'Delete' button");
        Assert.assertTrue(classPage.isDeleteAnnouncementModalDisplayed(), "Pop-Up of Delete Announcement must be visible");
        classPage.clickSubmitDeleteAnnouncement();

        logger.info("Expected Result: System show success message 'Success Delete Announcement'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Success Delete Announcement"), "The success message is mismatched");

        logger.info("User can delete class announcement: executed successfully");
    }
}
