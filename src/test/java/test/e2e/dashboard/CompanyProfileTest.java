package test.e2e.dashboard;

import core.BaseTest;
import core.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// FR-ID    : FR-DASH-01
// Module   : Dashboard
public class CompanyProfileTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private DashboardPage dashboardPage;

    private static final String EMAIL_REGEX = "^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$";
    private static final int MIN_PHONE_LENGTH = 8;
    private static final int MAX_PHONE_LENGTH = 15;

    // Pre-Condition : User already signed in
    @BeforeMethod
    public void setUp() {
        loginAsValidUser();
    }

    // Positive Test | P4
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-DASH-001 - User can view company profile")
    public void testUserCanViewCompanyProfile() {
        logger.info("TS-1: On the Dashboard page, locate page company name and profile");
        dashboardPage = new DashboardPage(DriverManager.getDriver());

        logger.info("Expected Result: System show company profile, vision, mission, culture, and total employee");

        boolean logoVisible = dashboardPage.isCompanyLogoDisplayed();
        String name = dashboardPage.getCompanyName();
        String address = dashboardPage.getCompanyAddress();
        String phone = dashboardPage.getCompanyPhone();
        String email = dashboardPage.getCompanyEmail();
        String vision = dashboardPage.getCompanyVision();
        String mission = dashboardPage.getCompanyMission();
        String culture = dashboardPage.getWorkCulture();
        String totalEmployee = dashboardPage.getTotalEmployee();

        logger.info("Actual Result: logo={}, name='{}', address='{}', phone='{}', email='{}', vision='{}', mission='{}', culture='{}', totalEmployee='{}'",
                logoVisible, name, address, phone, email, vision, mission, culture, totalEmployee);

        // Logo check
        Assert.assertTrue(logoVisible, "Company logo was not displayed");

        // Name check - not empty
        Assert.assertFalse(name.trim().isEmpty(), "Company name was empty");

        // Address check - not empty
        Assert.assertFalse(address.trim().isEmpty(), "Company address was empty");

        // Email check - not empty and valid format
        Assert.assertFalse(email.trim().isEmpty(), "Company email was empty");
        Assert.assertTrue(email.matches(EMAIL_REGEX),
                "Company email is not a valid email format: " + email);

        // Phone check - not empty and length within valid range
        Assert.assertFalse(phone.trim().isEmpty(), "Company phone was empty");
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        Assert.assertTrue(digitsOnly.length() >= MIN_PHONE_LENGTH && digitsOnly.length() <= MAX_PHONE_LENGTH,
                "Company phone number length is invalid. Expected between " + MIN_PHONE_LENGTH
                        + " and " + MAX_PHONE_LENGTH + " digits, but got " + digitsOnly.length()
                        + " digits: " + phone);

        // Vision / Mission / Culture - not empty
        Assert.assertFalse(vision.trim().isEmpty(), "Company Vision was empty");
        Assert.assertFalse(mission.trim().isEmpty(), "Company Mission was empty");
        Assert.assertFalse(culture.trim().isEmpty(), "Work Culture was empty");

        // Total Employee - not empty and numeric
        Assert.assertFalse(totalEmployee.trim().isEmpty(), "Total Employee was empty");
        Assert.assertTrue(totalEmployee.matches("\\d+"),
                "Total Employee should be a numeric value, but got: " + totalEmployee);

        logger.info("User can view company profile: executed successfully");
    }
}
