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
public class CreateAnnouncementTest extends BaseTest {
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
        announcementTitle = TestDataReader.getValue("valid-announcement-title");
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
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-009 - User can add class announcement with valid data")
    public void testUserCanAddClassAnnouncementWithValidData() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Announcement tab");
        classPage.openTabByTitle("Announcement");

        logger.info("TS-2: Scroll to the Create Announcement section");
        Assert.assertTrue(classPage.isClassAnnouncementSectionTitleDisplayed(), "Section title 'Manage Class' must be visible");

        logger.info("TS-3: Fill all the field (based on test data)");
        classPage.fillCreateAnnouncement(announcementTitle, announcementDesc);

        logger.info("TS-4: Click the 'Create Announcement' button");
        classPage.clickSubmitAnnouncement();

        logger.info("Expected Result: System show success message 'Succes Create Announcement'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Succes Create Announcement"), "The success message is mismatched");

        logger.info("User can add class announcement with valid data: executed successfully");
    }

    // Positive Test | P2 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-010 - User cant add class announcement with empty title")
    public void testUserCantAddClassAnnouncementWithEmptyTitle() {
        final String emptyAnnouncementTitle = "";

        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Announcement tab");
        classPage.openTabByTitle("Announcement");

        logger.info("TS-2: Scroll to the Create Announcement section");
        Assert.assertTrue(classPage.isClassAnnouncementSectionTitleDisplayed(), "Section title 'Announcement' must be visible");

        logger.info("TS-3: Fill the Announcement Description");
        classPage.fillCreateAnnouncement(emptyAnnouncementTitle, announcementDesc);

        logger.info("TS-4: Click the 'Create Announcement' button");
        classPage.clickSubmitAnnouncement();

        logger.info("Expected Result: System show success message 'Check field title'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Check field title"), "The success message is mismatched");

        logger.info("User cant add class announcement with empty title: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created announcement
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("testUserCanAddClassAnnouncementWithValidData")) {
            TestDataReader.setValue("created-announcement-title", announcementTitle);
        }
    }
}
