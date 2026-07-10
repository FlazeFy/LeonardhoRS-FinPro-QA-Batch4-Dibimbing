package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;
import core.DataGenerator;
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

// FR-ID    : FR-CLMG-07
// Module   : Class Management
public class CreateContentTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private AllPage allPage;

    // Test Data
    private String classTitle;
    private String contentTitle;
    private String contentDesc;
    private String liveClassDuration;
    private String contentLinkMeeting;
    private String contentPreTestUrl;
    private String checkInKey;
    private String invalidUrl;
    private String checkOutKey;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Test Data
        classTitle = TestDataReader.getValue("class-title");
        contentTitle = TestDataReader.getValue("valid-content-title");
        contentDesc = TestDataReader.getValue("content-desc");
        liveClassDuration = TestDataReader.getValue("content-live-class-duration");
        contentLinkMeeting = TestDataReader.getValue("valid-link");
        contentPreTestUrl = TestDataReader.getValue("valid-link");
        checkInKey = TestDataReader.getValue("check-in-key");
        checkOutKey = TestDataReader.getValue("check-out-key");
        invalidUrl = TestDataReader.getValue("invalid-link");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Content Title", "value", contentTitle),
                Map.of("key", "Content Description", "value", contentDesc),
                Map.of("key", "Live Class Duration", "value", liveClassDuration),
                Map.of("key", "Meeting Link", "value", contentLinkMeeting),
                Map.of("key", "Check In Key", "value", checkInKey),
                Map.of("key", "Check Out Key", "value", checkOutKey),
                Map.of("key", "Invalid Url", "value", invalidUrl)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-014 - User can add class content with valid data")
    public void testUserCanAddClassContentWithValidData() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("TS-2: Click 'Create Content' button");
        classPage.clickAddContentPageButton();

        logger.info("TS-3: Toggle active at Activate Attendance");
        classPage.setAttendanceEnabled(true);

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillCreateContent(
                contentTitle,
                contentDesc,
                checkInKey,
                checkOutKey,
                contentPreTestUrl,
                contentLinkMeeting,
                liveClassDuration,
                DataGenerator.getDateTimeFromNow(7)
        );

        logger.info("TS-5: Click 'Add Content' button");
        classPage.clickSubmitContent();

        logger.info("Expected Result: System redirected to Class Edit Page and at the Content tab and a success message 'Berhasil Membuat Class Content' appear");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Berhasil Membuat Class Content"), "The success message is mismatched");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("User can add class content with valid data: executed successfully");
    }

    // Negative Test | P3 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-017 - User cant add class content with invalid pre test url")
    public void testUserCantAddClassContentWithInvalidPreTestUrl() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("TS-2: Click 'Create Content' button");
        classPage.clickAddContentPageButton();

        logger.info("TS-3: Toggle active at Activate Attendance");
        classPage.setAttendanceEnabled(true);

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillCreateContent(
                contentTitle,
                contentDesc,
                checkInKey,
                checkOutKey,
                invalidUrl,
                contentLinkMeeting,
                liveClassDuration,
                DataGenerator.getDateTimeFromNow(7)
        );

        logger.info("TS-5: Click 'Add Content' button");
        classPage.clickSubmitContent();

        logger.info("Expected Result: System show failed message 'Pretest Url harus menggunakan url yang valid'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Pretest Url harus menggunakan url yang valid"), "The success message is mismatched");

        logger.info("User cant add class content with invalid pre test url: executed successfully");
    }

    // Negative Test | P2 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-018 - User cant add class content with invalid class meeting link")
    public void testUserCantAddClassContentWithInvalidClassMeetingLink() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("TS-2: Click 'Create Content' button");
        classPage.clickAddContentPageButton();

        logger.info("TS-3: Toggle active at Activate Attendance");
        classPage.setAttendanceEnabled(true);

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillCreateContent(
                contentTitle,
                contentDesc,
                checkInKey,
                checkOutKey,
                contentPreTestUrl,
                invalidUrl,
                liveClassDuration,
                DataGenerator.getDateTimeFromNow(7)
        );

        logger.info("TS-5: Click 'Add Content' button");
        classPage.clickSubmitContent();

        logger.info("Expected Result: System show failed message 'Zoom Url harus menggunakan url yang valid'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Zoom Url harus menggunakan url yang valid"), "The success message is mismatched");

        logger.info("User cant add class content with invalid class meeting link: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created content
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("testUserCanAddClassContentWithValidData")) {
            TestDataReader.setValue("created-content-title", contentTitle);
        }
    }
}
