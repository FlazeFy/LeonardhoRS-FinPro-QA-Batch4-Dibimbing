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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.e2e.dashboard.CompanyProfileTest;

import java.util.List;
import java.util.Map;

// FR-ID    : FR-CLMG-11
// Module   : Class Management
public class ViewAssignedClassMentorTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private ClassPage classPage;

    // Test Data
    private String classTitle;

    @BeforeMethod
    public void setUp() {
        // Pre-Condition : User already signed in
        loginAsValidUser();

        // Pre-Condition : User already select a class
        classTitle = TestDataReader.getValue("class-title");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
            Map.of("key", "Class Title", "value", classTitle)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);

        selectAClassByClassTitle(classTitle);
    }

    // Positive Test | P1
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-CLMG-034 - User can view assigned class mentor")
    public void testUserCanViewAssignedClassMentor() {
        classPage = new ClassPage(DriverManager.getDriver());

        logger.info("TS-1: On the Edit Class page, open the Mentor tab");
        classPage.openTabByTitle("Mentor");

        logger.info("TS-2: Scroll to the Mentor List section");
        Assert.assertTrue(classPage.isClassMentorSectionTitleDisplayed(), "Section title 'Announcement' must be visible");

        logger.info("TS-3: Observe the list of mentor displayed in table");
        logger.info("Expected Result: The mentor list is displayed and contains all required information (id, name, major, role, project assigned, test assigned, and action button)");
        Assert.assertTrue(classPage.isTableDisplayed(), "Table must be visible");
        Assert.assertTrue(classPage.isTableDataValid(), "All table body data must not empty");

        // Validate table data
        List<Map<String, String>> mentorData = classPage.getTableData();
        List<Map<String, Integer>> intFieldMentorData = mentorData.stream()
            .map(map -> Map.of(
                    "ID", Integer.parseInt(map.get("ID")),
                    "Projects Assigned", Integer.parseInt(map.get("Projects Assigned")),
                    "Tests Assigned", Integer.parseInt(map.get("Tests Assigned"))
            ))
            .toList();
        List<String> stringNullableFields = List.of("ID", "Projects Assigned", "Tests Assigned");
        TestUtil.validateColumn(intFieldMentorData, stringNullableFields, "number", false);

        logger.info("User can view assigned class mentor: executed successfully");
    }
}
