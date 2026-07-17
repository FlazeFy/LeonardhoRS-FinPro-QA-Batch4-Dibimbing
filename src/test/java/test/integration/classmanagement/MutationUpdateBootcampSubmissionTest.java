package test.integration.classmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import core.BaseApiTest;
import core.TestDataReader;
import core.TestUtil;

import static core.DataGenerator.getDateTimeFromNow;

public class MutationUpdateBootcampSubmissionTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(MutationUpdateBootcampSubmissionTest.class);

    private String submissionId;
    private String sid;
    private String classId;

    // Test Data
    private String submissionTitle;
    private String submissionDescription;
    private String assessmentDetail;
    private String assessmentAspect;
    private String maxScore;

    private static final String mutation = """
    mutation updateBootcampSubmission($input: InputBootcampSubmission!, $id: String!) {
      updateBootcampSubmission(input: $input, id: $id) {
        id
      }
    }
    """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: User already select a class and submission");
        classId = TestDataReader.getValue("created-bootcamp-id-api");
        submissionId = TestDataReader.getValue("created-submission-id-api");
        submissionTitle = TestDataReader.getValue("submission-title");
        submissionDescription = TestDataReader.getValue("submission-description");
        assessmentDetail = TestDataReader.getValue("assessment-detail");
        assessmentAspect = TestDataReader.getValue("assessment-aspect");
        maxScore = TestDataReader.getValue("max-score");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Submission Id", "value", submissionId),
                Map.of("key", "Class Id", "value", classId),
                Map.of("key", "Submission Title", "value", submissionTitle),
                Map.of("key", "Submission Description", "value", submissionDescription),
                Map.of("key", "Assessment Aspect", "value", assessmentAspect),
                Map.of("key", "Assessment Detail", "value", assessmentDetail),
                Map.of("key", "Max Score", "value", maxScore)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1 | Valid
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-070 - User can edit class submission with valid data")
    public void updateBootcampSubmission() {
        // Payload / Test Data can be found at Test Steps 4
        Map<String, Object> gradingDetail = new HashMap<>();
        gradingDetail.put("assessmentDetail", assessmentDetail + "-edit");
        gradingDetail.put("maxScore", Integer.parseInt(maxScore));

        Map<String, Object> gradingCriteria = new HashMap<>();
        gradingCriteria.put("assessmentAspect", assessmentAspect + "-edit");
        gradingCriteria.put("gradingDetail", List.of(gradingDetail));

        Map<String, Object> input = new HashMap<>();
        input.put("bootcampId", classId);
        input.put("title", submissionTitle + "-edit");
        input.put("description", "<p>" + submissionDescription + "-edit</p>");
        input.put("type", "baseProject");
        input.put("isNoPenalty", true);
        input.put("deadline", getDateTimeFromNow(14));
        input.put("isGroupSubmission", false);
        input.put("mentorId", null);
        input.put("attachmentUrl", new ArrayList<>());
        input.put("gradingCriteria", List.of(gradingCriteria));

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);
        variables.put("id", submissionId);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "updateBootcampSubmission", mutation, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.updateBootcampSubmission"));
        Assert.assertTrue(jsonPath.get("data.updateBootcampSubmission") instanceof Map);

        // Validate submission props
        Map<String, Object> dataObj = jsonPath.getMap("data.updateBootcampSubmission");

        List<String> stringFields = List.of("id");
        TestUtil.validateColumn(dataObj, stringFields, "string", false);

        // Validate each fields not empty / not whitespace only
        List<String> notEmptyStringFields = List.of("id");
        TestUtil.validateNotEmptyString(dataObj, notEmptyStringFields);

        logger.info("User can edit class submission with valid data: executed successfully");
    }
}
