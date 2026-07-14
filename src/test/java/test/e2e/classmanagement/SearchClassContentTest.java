package test.e2e.classmanagement;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.ClassPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;
import core.*;

public class SearchClassContentTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;
    private String contentTitle;
    private String invalidContentTitle;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Test Data
        classTitle = TestDataReader.getValue("class-title");
        contentTitle = TestDataReader.getValue("valid-content-title");
        invalidContentTitle = TestDataReader.getValue("class-content-invalid");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
            Map.of("key", "Class Title", "value", classTitle),
            Map.of("key", "Content Title", "value", contentTitle),
            Map.of("key", "Invalid Content Title", "value", invalidContentTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P3 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-021 - User can search class content with valid keyword")
    public void testUserCanSearchClassContentWithValidKeyword() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");

        logger.info("TS-2: Scroll to Content section");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("TS-3: Locate input with placeholder 'Search content'");
        Assert.assertTrue(classPage.isSearchClassContentDisplayed(), "Search bar class content must be visible");

        logger.info("TS-4: Type the content keyword");
        classPage.fillSearchClassContent(contentTitle);
        classPage.waitForPageLoading();

        logger.info("Expected Result: The content list is displayed, contains all required information (title and live class date) and content title related to search keyword");
        classPage.isContentListDisplayed();

        List<Map<String, String>> contentData = classPage.getContentCardData();
        TestUtil.validateNotEmptyString(contentData, null);

        for (Map<String, String> dt : contentData) {
            String expectedContentTitle = contentTitle.toLowerCase().trim();
            String actualContentTitle = dt.get("title").toLowerCase().trim();

            // Validate the content title
            Assert.assertTrue(actualContentTitle.contains(expectedContentTitle), "Expected "+expectedContentTitle+" to contain in "+actualContentTitle);

            // validate created at has format "d MMMM yyyy, H:mm"
            String date = dt.get("created-at");
            Assert.assertTrue(TestUtil.isValidDateFormat(date, "d MMMM yyyy, H:mm"), "Start date does not match expected format 'd MMMM yyyy, H:mm'");
        }

        logger.info("User can search class content with valid keyword: executed successfully");
    }

    // Negative Test | P4 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-022 - User can view no data message when no class found")
    public void testUserCanViewNoDataMessageWhenNoClassFound() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");

        logger.info("TS-2: Scroll to Content section");
        Assert.assertTrue(classPage.isClassContentSectionTitleDisplayed(), "Section title 'Content' must be visible");

        logger.info("TS-3: Locate input with placeholder 'Search content'");
        Assert.assertTrue(classPage.isSearchClassContentDisplayed(), "Search bar class content must be visible");

        logger.info("TS-4: Type the content keyword");
        classPage.fillSearchClassContent(invalidContentTitle);
        classPage.waitForPageLoading();

        logger.info("Expected Result: System show failed message 'Class content not found'");
        Assert.assertTrue(classPage.isClassContentFailedMessageDisplayed("Class content not found"), "The failed message must be visible and match 'Class content not found'");

        logger.info("User can view no data message when no class found: executed successfully");
    }
}
