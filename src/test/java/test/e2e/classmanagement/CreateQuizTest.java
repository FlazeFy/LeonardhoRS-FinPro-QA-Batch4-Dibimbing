package test.e2e.classmanagement;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.AllPage;
import org.example.page.ClassPage;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;
import core.*;

// FR-ID    : FR-CLMG-06
// Module   : Class Management
public class CreateQuizTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private AllPage allPage;

    // Test Data
    private String classTitle;
    private String testTitle;
    private String contentTitle;
    private String testType;
    private String duration;
    private String mentorName;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Test Data
        classTitle = TestDataReader.getValue("class-title");
        testTitle = TestDataReader.getValue("valid-test-title");
        contentTitle = TestDataReader.getValue("created-content-title");
        duration = TestDataReader.getValue("test-duration");
        testType = TestDataReader.getValue("test-type");
        mentorName = TestDataReader.getValue("valid-add-mentor-name");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Test Title", "value", testTitle),
                Map.of("key", "Content Title", "value", contentTitle),
                Map.of("key", "Test Type", "value", testType),
                Map.of("key", "Duration", "value", duration),
                Map.of("key", "Mentor Name", "value", mentorName)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-048 - User can add class test with valid data")
    public void testUserCanAddClassTestWithValidData() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Test tab");
        classPage.openTabByTitle("Test");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'Test' must be visible");

        logger.info("TS-2: Click 'Create Test' button");
        classPage.clickAddTestButton();

        logger.info("TS-3: Select a test test type");
        classPage.setTestContentType("Class Test");

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillCreateTest(
                testTitle,
                testType,
                "Timer",
                duration,
                mentorName,
                DataGenerator.getDateTimeFromNow(7),
                "Class Test"
        );

        logger.info("TS-5: Click 'Create Test'");
        classPage.clickSubmitTest();

        logger.info("Expected Result: System redirected to Class Edit Page and at the Test tab and a success message 'Succes Create Test' appear");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Succes Create Test"), "The success message is mismatched");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'Test' must be visible");

        logger.info("User can add class test with valid data: executed successfully");
    }

    // Negative Test | P2 | Invalid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-049 - User cant add class test with invalid empty title")
    public void testUserCantAddClassTestWithInvalidEmptyTitle() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Test tab");
        classPage.openTabByTitle("Test");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'Test' must be visible");

        logger.info("TS-2: Click 'Create Test' button");
        classPage.clickAddTestButton();

        logger.info("TS-3: Select a test test type");
        classPage.setTestContentType("Class Test");

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillCreateTest(
                "",
                testType,
                "Timer",
                duration,
                mentorName,
                DataGenerator.getDateTimeFromNow(7),
                "Class Test"
        );

        logger.info("TS-5: Click 'Create Test'");
        classPage.clickSubmitTest();

        logger.info("Expected Result: System show failed message 'Check field title'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Check field title"), "The success message is mismatched");

        logger.info("User cant add class test with invalid empty title: executed successfully");
    }

    // Positive Test | P1 | Valid
    @Test(priority = 2, groups = {"ui-test"}, description = "TC-CLMG-054 - User can add content test with valid data")
    public void testUserCanAddContentTestWithValidData() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Test tab");
        classPage.openTabByTitle("Test");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'Test' must be visible");

        logger.info("TS-2: Click 'Create Test' button");
        classPage.clickAddTestButton();

        logger.info("TS-3: Select a test test type");
        classPage.setTestContentType("Content Test");

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillCreateTest(
                contentTitle,
                testType,
                "Timer",
                duration,
                mentorName,
                DataGenerator.getDateTimeFromNow(7),
                "Content Test"
        );

        logger.info("TS-5: Click 'Create Test'");
        classPage.clickSubmitTest();

        logger.info("Expected Result: System redirected to Class Edit Page and at the Test tab and a success message 'Succes Create Test' appear");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Succes Create Test"), "The success message is mismatched");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'Test' must be visible");

        logger.info("User can add content test with valid data: executed successfully");
    }

    // Negative Test | P2 | Invalid
    @Test(priority = 2, groups = {"ui-test"}, description = "TC-CLMG-055 - User cant add content test with unselected class content")
    public void testUserCantAddContentTestWithUnselectedClassContent() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Test tab");
        classPage.openTabByTitle("Test");
        Assert.assertTrue(classPage.isClassTestSectionTitleDisplayed(), "Section title 'Test' must be visible");

        logger.info("TS-2: Click 'Create Test' button");
        classPage.clickAddTestButton();

        logger.info("TS-3: Select a test test type");
        classPage.setTestContentType("Content Test");

        logger.info("TS-4: Fill all the field (based on test data)");
        classPage.fillCreateTest(
                "",
                testType,
                "Timer",
                duration,
                mentorName,
                DataGenerator.getDateTimeFromNow(7),
                "Content Test"
        );

        logger.info("TS-5: Click 'Create Test'");
        classPage.clickSubmitTest();

        logger.info("Expected Result: System show failed message 'Check field class content'");
        allPage = new AllPage(DriverManager.getDriver());
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Check field class content"), "The success message is mismatched");

        logger.info("User cant add content test with unselected class content: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created test
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("testUserCanAddClassTestWithValidData")) {
            TestDataReader.setValue("created-test-title-class", testTitle);
        }

        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("testUserCanAddContentTestWithValidData")) {
            TestDataReader.setValue("created-test-title-content", contentTitle);
        }
    }
}
