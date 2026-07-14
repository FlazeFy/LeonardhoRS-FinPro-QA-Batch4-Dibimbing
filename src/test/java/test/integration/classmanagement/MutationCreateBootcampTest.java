package test.integration.classmanagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import core.BaseApiTest;
import core.TestDataReader;
import core.TestUtil;
import core.DataGenerator;

import static core.DataGenerator.getDateTimeFromNow;

public class MutationCreateBootcampTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationCreateBootcampTest.class);

    private String sid;
    private String bootcampId;

    // Test Data
    private String title;
    private String descriptions;
    private String divisionId;
    private String enrollmentKey;
    private Integer angkatanId;

    private static final String mutation = """
    mutation createBootcamp($input: InputBootcamp!) {
      createBootcamp(input: $input) {
        id
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        String uniqueChar = DataGenerator.getUniqueChar();
        title = TestDataReader.getValue("bootcamp-title") + "-" + uniqueChar;
        descriptions = TestDataReader.getValue("bootcamp-description");
        divisionId = TestDataReader.getValue("division-id");
        enrollmentKey = TestDataReader.getValue("bootcamp-enrollment-key");
        angkatanId = Integer.parseInt(TestDataReader.getValue("angkatan-id"));

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Bootcamp Title", "value", title),
                Map.of("key", "Description", "value", descriptions),
                Map.of("key", "Division Id", "value", divisionId),
                Map.of("key", "Enrollment Key", "value", enrollmentKey),
                Map.of("key", "Angkatan Id", "value", String.valueOf(angkatanId))
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-002 - User can add a class with valid data")
    public void createBootcamp() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> input = new HashMap<>();
        input.put("title", title);
        input.put("descriptions", "<p>" + descriptions + "</p>");
        input.put("angkatanId", angkatanId);
        input.put("divisionId", divisionId);
        input.put("startedAt", getDateTimeFromNow(1));
        input.put("finishedAt", getDateTimeFromNow(30));
        input.put("enrollmentKey", enrollmentKey);
        input.put("syllabus", "");
        input.put("learningContract", "");

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Request
        Response response = TestUtil.templateGraphQLRequest("createBootcamp", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid);
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.createBootcamp"));
        Assert.assertTrue(jsonPath.get("data.createBootcamp") instanceof Map);

        // Validate bootcamp props
        Map<String, Object> dataObj = jsonPath.getMap("data.createBootcamp");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        bootcampId = (String) dataObj.get("id");

        logger.info("User can add a class with valid data: executed successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Store created bootcamp
        if (result.getStatus() == ITestResult.SUCCESS &&
                result.getMethod().getMethodName().equals("createBootcamp")) {

            TestDataReader.setValue("created-bootcamp-id-api", bootcampId);
            TestDataReader.setValue("created-bootcamp-title-api", title);
        }
    }
}
