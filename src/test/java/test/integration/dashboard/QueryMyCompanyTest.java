package test.integration.dashboard;

import core.BaseApiTest;
import core.TestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;

public class QueryMyCompanyTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryMyCompanyTest.class);

    private static final String query = """
        query MyCompany {
          myCompany {
            id slug name logoUrl address phoneNumber email vision mission workCulture
          }
        }
        """;

    // Positive Test | P4
    @Test(priority = 1, groups = {"api-test"}, description = "TC-DASH-001 - User can view company profile")
    public void myCompanyValidData() {
        logger.info("Pre-Condition: User already signed in");
        final String sid = TestUtil.getSid();

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "myCompany", query, null, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.myCompany"));
        Map<String, Object> company = jsonPath.getMap("data.myCompany");

        // Validate myCompany props
        // Validate each fields data type
        List<String> stringNullableFields = List.of("slug", "name", "logoUrl", "address", "phoneNumber", "email", "vision", "mission", "workCulture");
        TestUtil.validateColumn(company, stringNullableFields, "string", true);

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(company, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("name", "address", "email", "phoneNumber", "vision", "mission", "workCulture");
        TestUtil.validateNotEmptyString(company, notEmptyStringFields);

        logger.info("User can view company profile: executed successfully");
    }
}
