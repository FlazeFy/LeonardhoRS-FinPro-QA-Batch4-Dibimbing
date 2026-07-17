package test.e2e.classmanagement;

import java.util.List;
import java.util.Map;

import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.AllPage;
import org.example.page.ClassPage;
import org.example.page.DashboardPage;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

// FR-ID    : FR-CLMG-02
// Module   : Class Management
public class CreateClassTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;
    private DashboardPage dashboardPage;
    private AllPage allPage;

    // Test Data
    private String classTitle;
    private String classDesc;
    private String batch;
    private String major;
    private String enrollmentKey;
    private String classSyllabusType;
    private String classSyllabusUrl;
    private String assessmentComponentSelected;
    private String assessmentComponentPercentage;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Test Data
        classTitle = TestDataReader.getValue("valid-class-title");
        classDesc = TestDataReader.getValue("class-desc");
        batch = TestDataReader.getValue("class-batch");
        major = TestDataReader.getValue("class-major");
        enrollmentKey = TestDataReader.getValue("class-enrollment-key");
        classSyllabusType = TestDataReader.getValue("class-syllabus-type");
        classSyllabusUrl = TestDataReader.getValue("class-syllabus-url");
        assessmentComponentSelected = TestDataReader.getValue("assessment-component-selected");
        assessmentComponentPercentage = TestDataReader.getValue("assessment-component-percentage");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Title", "value", classTitle),
                Map.of("key", "Class Description", "value", classDesc),
                Map.of("key", "Class Batch", "value", batch),
                Map.of("key", "Class Major", "value", major),
                Map.of("key", "Enrollment Key", "value", enrollmentKey),
                Map.of("key", "Class Syllabus Type", "value", classSyllabusType),
                Map.of("key", "Class Syllabus URL", "value", classSyllabusUrl),
                Map.of("key", "Assessment Component", "value", assessmentComponentSelected),
                Map.of("key", "Assessment Percentage", "value", assessmentComponentPercentage)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-002 - User can add a class with valid data")
    public void testUserCanAddAClassWithValidData() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Dashboard page, click the 'Class' menu in the left sidebar");
        dashboardPage = new DashboardPage(DriverManager.getDriver());
        dashboardPage.clickClassMenuButton();

        logger.info("TS-2: Locate the section titled 'Manage Class'");
        Assert.assertTrue(classPage.isClassManagementSectionTitleDisplayed(), "Section title 'Manage Class' must be visible");

        logger.info("TS-3: Click 'Add New Class'");
        classPage.openClassCreatePage();

        logger.info("TS-4: Fill all the field (based on test data)");
        Assert.assertTrue(classPage.isCreateClassPageTitleDisplayed(), "Create Class Page Title must be visible");
        classPage.fillCreateClass(
                classTitle,
                classDesc,
                batch,
                major,
                DataGenerator.getDateTimeFromNow(7),
                DataGenerator.getDateTimeFromNow(30),
                enrollmentKey,
                classSyllabusType,
                classSyllabusUrl
        );

        logger.info("TS-5: Click 'Next'");
        classPage.clickSubmitCreateClassForm();

        logger.info("TS-6: Fill all the assessment field (based on test data)");
        classPage.fillCreateClassAssessment(
                assessmentComponentSelected,
                assessmentComponentPercentage
        );

        logger.info("TS-7: Click 'Next'");
        classPage.clickSubmitCreateClassAssessmentForm();

        logger.info("TS-8: Click 'Save Class'");
        classPage.clickSubmitCreateClassReviewForm();

        logger.info("Expected Result: System redirected to Class Edit Page");
        allPage = new AllPage(DriverManager.getDriver());
        allPage.waitForUrlToContain("detail");
        Assert.assertTrue(allPage.getResponsePopUpText().contains("Succes Save Changes"), "The success message is mismatched");

        logger.info("User can add a class with valid data: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created announcement
        if (result.getStatus() == ITestResult.SUCCESS && result.getMethod().getMethodName().equals("testUserCanAddAClassWithValidData")) {
            TestDataReader.setValue("class-title", classTitle);
        }
    }
}
