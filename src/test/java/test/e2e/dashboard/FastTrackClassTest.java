package test.e2e.dashboard;

import core.BaseTest;
import core.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// FR-ID    : FR-DASH-02
// Module   : Dashboard
public class FastTrackClassTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CompanyProfileTest.class);
    private DashboardPage dashboardPage;

    // Pre-Condition : User already signed in
    @BeforeMethod
    public void setUp() {
        loginAsValidUser();
    }

    // Positive Test | P3
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-DASH-002 - User can view fast track summary")
    public void testUserCanViewFastTrackSummary() {
        logger.info("TS-1: On the Dashboard page, locate the sections titled 'Total Fast Track Class,' 'Fast Track Class Active,' and 'Fast Track Class Finished'");
        dashboardPage = new DashboardPage(DriverManager.getDriver());

        logger.info("Expected Result: System show total fast track class, total active fast track, and total finished fast track class");

        String totalFastTrack = dashboardPage.getTotalFastTrack();
        String totalFastTrackClass = dashboardPage.getTotalFastTrackClass();
        String totalFastTrackClassFinished = dashboardPage.getFastTrackClassFinished();

        logger.info("Actual Result: Total Fast Track Class={}, Total Fast Track Class='{}', Fast Track Class Finished='{}'", totalFastTrack, totalFastTrackClass, totalFastTrackClassFinished);

        // Total Fast Track - not empty and numeric
        Assert.assertFalse(totalFastTrack.trim().isEmpty(), "Total Fast Track was empty");
        Assert.assertTrue(totalFastTrack.matches("\\d+"),
                "Total Fast Track should be a numeric value, but got: " + totalFastTrack);

        // Total Fast Track Class - not empty and numeric
        Assert.assertFalse(totalFastTrackClass.trim().isEmpty(), "Total Fast Track Class was empty");
        Assert.assertTrue(totalFastTrackClass.matches("\\d+"),
                "Total Fast Track Class should be a numeric value, but got: " + totalFastTrackClass);

        // Total Fast Track Class Finished - not empty and numeric
        Assert.assertFalse(totalFastTrackClassFinished.trim().isEmpty(), "Total Fast Track Class Finished was empty");
        Assert.assertTrue(totalFastTrackClassFinished.matches("\\d+"),
                "Total Fast Track Class Finished should be a numeric value, but got: " + totalFastTrackClassFinished);

        logger.info("User can view fast track summary: executed successfully");
    }

    // Negative Test | P2
    @Test(priority = 1, groups = {"ui-test"}, description = "TC-DASH-004 User can view no data message when no nearest fast track class is available")
    public void testUserCanViewNoDataMessageWhenNoNearestFastTrackClassIsAvailable() {
        logger.info("TS-1: On the Dashboard page, locate the sections titled 'Nearest Fast Track Class'");
        dashboardPage = new DashboardPage(DriverManager.getDriver());

        logger.info("Expected Result: System show message 'You don't have a nearest fast track class schedule yet'");

        String nearestFastTrackClassMessage = dashboardPage.getNearestFastTrackClassMessage();

        logger.info("Actual Result: Nearest Fast Track Class Message={}", nearestFastTrackClassMessage);

        // Nearest Fast Track Class Message - not empty and equal to error message
        Assert.assertEquals(dashboardPage.getNearestFastTrackClassMessage(), "You don't have a nearest fast track class schedule yet", "The error message is mismatched");

        logger.info("User can view no data message when no nearest fast track class is available: executed successfully");
    }
}
