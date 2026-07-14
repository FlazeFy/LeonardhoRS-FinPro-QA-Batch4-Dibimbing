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

public class ToggleContentVisibilityTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private AllPage allPage;

    // Test Data
    private String classTitle;
    private String contentTitle;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Test Data
        classTitle = TestDataReader.getValue("class-title");
        // Pre-Condition : At least one class content exists
        contentTitle = TestDataReader.getValue("valid-content-title");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Content Title", "value", contentTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P2
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-027 - User can hide class content")
    public void testUserCanHideClassContent() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");

        logger.info("TS-2: Click a content");
        classPage.fillSearchClassContent(contentTitle);
        classPage.waitForPageLoading();
        classPage.clickAContent(contentTitle);

        logger.info("TS-3: Click 'Hide Content' button");
        classPage.toggleHideContent(contentTitle, "Hide Content");

        logger.info("Expected Result: The content button show title 'Show Content'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Succes Save Changes"), "The success message is mismatched");
        Assert.assertTrue(classPage.isContentToggleButtonDisplayed(contentTitle, "Show Content"), "Toggle button current status must be marked as 'Show Content'");

        logger.info("User can hide class content: executed successfully");
    }

    // Positive Test | P2
    @Test(priority = 2, groups = {"ui-test"}, description = "TC-CLMG-028 - User can unhide class content")
    public void testUserCanUnhideClassContent() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");

        logger.info("TS-2: Click a content");
        classPage.fillSearchClassContent(contentTitle);
        classPage.waitForPageLoading();
        classPage.clickAContent(contentTitle);

        logger.info("TS-3: Click 'Show Content' button");
        classPage.toggleHideContent(contentTitle, "Show Content");

        logger.info("Expected Result: The content button show title 'Hide Content'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Succes Save Changes"), "The success message is mismatched");
        Assert.assertTrue(classPage.isContentToggleButtonDisplayed(contentTitle, "Hide Content"), "Toggle button current status must be marked as 'Hide Content'");

        logger.info("User can unhide class content: executed successfully");
    }
}
