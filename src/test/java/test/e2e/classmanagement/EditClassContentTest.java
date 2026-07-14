package test.e2e.classmanagement;

import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;
import core.DataGenerator;
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

// FR-ID    : FR-CLMG-07
// Module   : Class Management
public class EditClassContentTest extends BaseTest {
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
                Map.of("key", "Content Pre Test Url", "value", contentPreTestUrl),
                Map.of("key", "Check In Key", "value", checkInKey),
                Map.of("key", "Check Out Key", "value", checkOutKey),
                Map.of("key", "Invalid Url", "value", invalidUrl)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Negative Test | P3 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-030 - User cant edit class content with invalid pre test url")
    public void testUserCantEditClassContentWithInvalidPreTestUrl() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("TS-2: Click a content");
        classPage.fillSearchClassContent(contentTitle);
        classPage.waitForPageLoading();
        classPage.clickAContent(contentTitle);

        logger.info("TS-3: Click 'Edit Content' button");
        classPage.openEditContent(contentTitle);
        Assert.assertTrue(classPage.isClassUpdateContentSectionTitleDisplayed(), "Section title 'Update Content' must be visible");

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillEditContent(
                contentTitle,
                contentDesc,
                checkInKey,
                checkOutKey,
                invalidUrl,
                contentLinkMeeting,
                liveClassDuration,
                DataGenerator.getDateTimeFromNow(7)
        );

        logger.info("TS-5: Click 'Update Content' button");
        classPage.clickSubmitUpdateContent();

        logger.info("Expected Result: System show failed message 'Pretest Url harus menggunakan url yang valid'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Pretest Url harus menggunakan url yang valid"), "The failed message is mismatched");
        Assert.assertTrue(classPage.isClassUpdateContentSectionTitleDisplayed(), "Section title 'Update Content' must be visible");

        logger.info("User cant edit class content with invalid pre test url: executed successfully");
    }

    // Negative Test | P4 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-031 - User cant edit class content with invalid class meeting link")
    public void testUserCanEditClassContentWithInvalidClassMeetingLink() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("TS-2: Click a content");
        classPage.fillSearchClassContent(contentTitle);
        classPage.waitForPageLoading();
        classPage.clickAContent(contentTitle);

        logger.info("TS-3: Click 'Edit Content' button");
        classPage.openEditContent(contentTitle);
        Assert.assertTrue(classPage.isClassUpdateContentSectionTitleDisplayed(), "Section title 'Update Content' must be visible");

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

        logger.info("TS-5: Click 'Update Content' button");
        classPage.clickSubmitUpdateContent();

        logger.info("Expected Result: System show failed message 'Zoom Url harus menggunakan url yang valid'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Zoom Url harus menggunakan url yang valid"), "The failed message is mismatched");
        Assert.assertTrue(classPage.isClassUpdateContentSectionTitleDisplayed(), "Section title 'Update Content' must be visible");

        logger.info("User cant edit class content with invalid class meeting link: executed successfully");
    }
}
