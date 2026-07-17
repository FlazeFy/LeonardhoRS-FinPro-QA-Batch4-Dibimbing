package test.integration.classmanagement;

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

public class QueryBootcampSubmissionsTest extends BaseApiTest {
    private static final Logger logger = LogManager.getLogger(QueryBootcampSubmissionsTest.class);
    private String sid;
    private String classId;

    private static final String query = """
        query bootcampSubmissions($bootcampId: String!, $param: BootcampSubmissionBaseQuery!) {
          bootcampSubmissions(bootcampId: $bootcampId, param: $param) {
            id title description type isNoPenalty isSubmitted submissionType deadline isGroupSubmission attachmentUrl isGradingFinish mentorId
            gradingCriteria {
              assessmentAspect gradingDetail {
                assessmentDetail maxScore
              }
            }
          }
        }
        """;

    @BeforeMethod
    public void setUp() {
        logger.info("Pre-Condition: User already signed in");
        sid = TestUtil.getSid();

        logger.info("Pre-Condition: User already select a class");
        classId = TestDataReader.getValue("created-bootcamp-id-api");

        // Validate each test data
        List<Map<String, String>> notEmptyFields = List.of(
                Map.of("key", "Class Id", "value", classId)
        );
        TestUtil.validateNotEmptyString(notEmptyFields, null);
    }

    // Positive Test | P1
    @Test(priority = 1, groups = {"api-test"}, description = "TC-CLMG-059 - User can view class submission")
    public void bootcampSubmissions() {
        // Params
        Map<String, Object> param = new HashMap<>();
        param.put("orderBy", "DESC");
        param.put("orderColumn", "createdAt");

        Map<String, Object> variables = new HashMap<>();
        variables.put("bootcampId", classId);
        variables.put("param", param);

        // Request
        Response response = TestUtil.templateGraphQLRequest(
                "bootcampSubmissions", query, variables, config.getProperty("usernameGraphQl"), config.getProperty("passwordGraphQl"), sid
        );
        JsonPath jsonPath = response.jsonPath();

        // Validate base structure
        Assert.assertNotNull(jsonPath.get("data.bootcampSubmissions"));
        List<Map<String, Object>> submissions = jsonPath.getList("data.bootcampSubmissions");

        // Validate submission props
        List<String> stringFields = List.of("id");
        List<String> stringNullableFields = List.of("title", "description", "type", "submissionType", "deadline", "mentorId");
        List<String> booleanFields = List.of("isNoPenalty", "isSubmitted", "isGroupSubmission", "isGradingFinish");
        TestUtil.validateColumn(submissions, stringFields, "string", false);
        TestUtil.validateColumn(submissions, stringNullableFields, "string", true);
        TestUtil.validateColumn(submissions, booleanFields, "boolean", false);
        TestUtil.validateNotEmptyString(submissions, List.of("id"));

        // Validate attachmentUrl
        for (Map<String, Object> submission : submissions) {
            Object attachmentObj = submission.get("attachmentUrl");
            Assert.assertTrue(attachmentObj instanceof List, "attachmentUrl must be an array");
        }

        // Validate gradingCriteria
        for (Map<String, Object> submission : submissions) {
            Object gradingCriteriaObj = submission.get("gradingCriteria");
            Assert.assertTrue(gradingCriteriaObj instanceof List, "gradingCriteria must be an array");

            List<Map<String, Object>> gradingCriteria = (List<Map<String, Object>>) gradingCriteriaObj;
            TestUtil.validateColumn(gradingCriteria, List.of("assessmentAspect"), "string", false);
            TestUtil.validateNotEmptyString(gradingCriteria, List.of("assessmentAspect"));

            for (Map<String, Object> criteria : gradingCriteria) {
                Object gradingDetailObj = criteria.get("gradingDetail");
                Assert.assertTrue(gradingDetailObj instanceof List, "gradingDetail must be an array");

                List<Map<String, Object>> gradingDetails = (List<Map<String, Object>>) gradingDetailObj;
                TestUtil.validateColumn(gradingDetails, List.of("assessmentDetail"), "string", false);
                TestUtil.validateColumn(gradingDetails, List.of("maxScore"), "number", false);
                TestUtil.validateNotEmptyString(gradingDetails, List.of("assessmentDetail"));
            }
        }

        logger.info("User can view class submission: executed successfully");
    }
}
