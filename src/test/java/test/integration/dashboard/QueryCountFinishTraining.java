package test.integration.dashboard;

import core.BaseApiTest;
import core.TestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import static core.TestUtil.templateGraphQLRequest;

public class QueryCountFinishTraining extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompany.class);

    private static final String query = """
          query countFinishTraining {
            countFinishTraining
          }
        """;

    // Positive Test | P3
    @Test(priority = 1, groups = {"api-test"}, description = "TC-DASH-002 - User can view fast track summary")
    public void countFinishTrainingValidData() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Request
        Response response = templateGraphQLRequest("countFinishTraining", query, null, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid);
        JsonPath jsonPath = response.jsonPath();

        // Validate total employee
        Object countFinishTraining = jsonPath.get("data.countFinishTraining");
        Assert.assertNotNull(countFinishTraining);
        Assert.assertTrue(countFinishTraining instanceof Integer, "countFinishTraining should be an Integer");

        int count = (int)countFinishTraining;
        Assert.assertTrue(count >= 0,  "countFinishTraining should be greater than or equal to 0");

        logger.info("User can view fast track summary: executed successfully");
    }
}
