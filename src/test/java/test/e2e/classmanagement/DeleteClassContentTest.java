package test.e2e.classmanagement;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.AllPage;
import org.example.page.ClassPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;
import core.BaseTest;
import core.DriverManager;
import core.TestDataReader;
import core.TestUtil;

public class DeleteClassContentTest extends BaseTest {
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
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-026 - User can delete class content")
    public void testUserCanDeleteClassContent() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Content tab");
        classPage.openTabByTitle("Content");

        logger.info("TS-2: Click a content");
        classPage.fillSearchClassContent(contentTitle);
        classPage.waitForPageLoading();
        classPage.clickAContent(contentTitle);

        logger.info("TS-3: Click 'Delete' button");
        classPage.deleteAContent(contentTitle);

        logger.info("Expected Result: System show success message 'Success Save Changes'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Berhasil Membuat Class Content"), "The success message is mismatched");

        logger.info("User can delete class content: executed successfully");
    }
}
