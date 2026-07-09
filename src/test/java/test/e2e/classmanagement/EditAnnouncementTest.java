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
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

// FR-ID    : FR-CLMG-05
// Module   : Class Management
public class EditAnnouncementTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private AllPage allPage;

    // Test Data
    private String classTitle;
    private String announcementTitle;
    private String announcementDesc;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");
        // Pre-Condition : At least one announcement exists
        announcementTitle = TestDataReader.getValue("created-announcement-title");
        // Test Data
        announcementDesc = TestDataReader.getValue("valid-announcement-desc");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Announcement Title", "value", announcementTitle),
                Map.of("key", "Announcement Desc", "value", announcementDesc)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P2 | Valid
    @Test(priority = 2, groups = {"ui-test"}, description = "TC-CLMG-011 - User can edit class announcement with valid data")
    public void testUserCanEditClassAnnouncementWithValidData() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Announcement tab");
        classPage.openTabByTitle("Announcement");

        logger.info("TS-2: Below the Create Announcement form. Select an announcement");
        classPage.findAnnouncementByTitle(announcementTitle);

        logger.info("TS-3: Click the 'Edit' button");
        classPage.clickEditAnnouncementByTitle(announcementTitle);

        logger.info("TS-4: On the Edit Announcement pop-up. Change the Announcement Title and Announcement Description");
        classPage.fillEditAnnouncement(announcementTitle, announcementDesc);

        logger.info("TS-5: Click the 'Edit Announcement' button");
        classPage.clickSubmitEditAnnouncement();

        logger.info("Expected Result: System show success message 'Succes Update Announcement'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Succes Update Announcement"), "The success message is mismatched");

        logger.info("User can edit class announcement with valid data: executed successfully");
    }

    // Negative Test | P2 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-012 - User cant edit class announcement with empty title")
    public void testUserCantEditClassAnnouncementWithEmptyTitle() {
        final String emptyAnnouncementTitle = "";

        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Announcement tab");
        classPage.openTabByTitle("Announcement");

        logger.info("TS-2: Below the Create Announcement form. Select an announcement");
        classPage.findAnnouncementByTitle(announcementTitle);

        logger.info("TS-3: Click the 'Edit' button");
        classPage.clickEditAnnouncementByTitle(announcementTitle);

        logger.info("TS-4: On the Edit Announcement pop-up. Change the field especially clear the Announcement Title");
        classPage.fillEditAnnouncement(emptyAnnouncementTitle, announcementDesc);

        logger.info("TS-5: Click the 'Edit Announcement' button");
        classPage.clickSubmitEditAnnouncement();

        logger.info("Expected Result: System show success message 'Check field title'");
        allPage = new AllPage(DriverManager.getDriver());
        System.out.println(allPage.getResponsePopUpText());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Check field title"), "The success message is mismatched");

        logger.info("User cant edit class announcement with empty title: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store edited announcement
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("testUserCanEditClassAnnouncementWithValidData")) {
            TestDataReader.setValue("created-announcement-title", announcementTitle);
        }
    }
}
