package core;

import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import org.testng.Assert;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static io.restassured.RestAssured.given;

public class TestUtil extends BaseApiTest{
    // API Test
    public static Response templateGraphQLRequest(String endpointName, String graphqlQuery, Object variables, String username, String password, String sessionId) {
        String contentType = "application/json";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", graphqlQuery);
        if (variables != null) requestBody.put("variables", variables);

        RequestSpecification request = given().contentType(contentType);
        // Basic Auth
        if (username != null && !username.isBlank()) request.auth().preemptive().basic(username, password);
        // Session Cookie
        if (sessionId != null && !sessionId.isBlank()) request.cookie("sid_b2b", sessionId);

        Response response = request
                .body(requestBody)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("==== GraphQL : " + endpointName + " ====");
        System.out.println(response.asPrettyString());

        return response;
    }

    public static void validateColumn(Object data, List<String> fields, String dataType, boolean nullable) {
        // Convert object to list
        List<Map<String, Object>> dataArray;
        dataArray = data instanceof List ? (List<Map<String, Object>>) data : List.of((Map<String, Object>) data);

        // Loop item
        for (Map<String, Object> item : dataArray) {
            // Validate object
            Assert.assertNotNull(item);

            // Loop fields
            for (String field : fields) {
                // Validate field exists
                Assert.assertTrue(item.containsKey(field), "Missing field: " + field);
                Object value = item.get(field);

                // Nullable validation
                if (nullable && value == null) {
                    Assert.assertNull(value);
                    continue;
                }

                // Validate datatype
                switch (dataType) {
                    case "string":
                        Assert.assertTrue(value instanceof String, field + " is not String");
                        break;

                    case "number":
                        Assert.assertTrue(value instanceof Number, field + " is not Number");

                        // Validate integer or decimal
                        if (value instanceof Integer || value instanceof Long) {
                            Assert.assertEquals(((Number) value).doubleValue() % 1, 0.0);
                        } else {
                            Assert.assertNotEquals(((Number) value).doubleValue() % 1, 0.0);
                        }
                        break;

                    case "boolean":
                        Assert.assertTrue(value instanceof Boolean, field + " is not Boolean");
                        break;

                    case "bool_number":
                        Assert.assertTrue(value instanceof Number, field + " is not Number");
                        int numberValue = ((Number) value).intValue();

                        Assert.assertTrue(numberValue == 0 || numberValue == 1, field + " is not 0 or 1");
                        break;

                    default:
                        Assert.fail("Unsupported data type: " + dataType);
                }
            }
        }
    }

    public static void validateAPIFailed(JsonPath jsonPath, String message) {
        // Validate base structure
        Assert.assertNull(jsonPath.get("data"));
        Assert.assertNotNull(jsonPath.get("errors"));
        Assert.assertTrue(jsonPath.get("errors") instanceof List);

        // Validate error message / Expected Result
        List<Map<String, Object>> errors = jsonPath.getList("errors");
        Assert.assertFalse(errors.isEmpty());

        Assert.assertEquals(errors.get(0).get("message"), message, "The error message is mismatched");
    }

    public static String getSid() {
        final String mutation = """
            mutation Login($companyId: String!, $usernameOrEmail: String!, $password: String!) {
               login(companyId: $companyId, usernameOrEmail: $usernameOrEmail, password: $password) {
                 user { id }
                 errors { field message }
               }
             }
            """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("usernameOrEmail", config.getProperty("validEmailAuth"));
        variables.put("password", config.getProperty("validPasswordAuth"));
        variables.put("companyId", config.getProperty("companyId"));

        Response response = templateGraphQLRequest("login", mutation, variables, null, null, null);
        String sid = response.getCookie("sid_b2b");

        return sid;
    }

    public static void validateNotEmptyString(Object data, List<String> fields) {
        // Convert object to list
        List<Map<String, Object>> dataArray;
        dataArray = data instanceof List ? (List<Map<String, Object>>) data : List.of((Map<String, Object>) data);

        // Loop item
        for (Map<String, Object> item : dataArray) {
            // Validate object
            Assert.assertNotNull(item);

            // If fields is null, validate all fields
            List<String> validateFields = fields != null ? fields : new ArrayList<>(item.keySet());

            // Loop fields
            for (String field : validateFields) {
                // Validate field exists
                Assert.assertTrue(item.containsKey(field), "Missing field: " + field);
                Object value = item.get(field);

                // Validate datatype
                Assert.assertTrue(value instanceof String, field + " is not String");

                // Validate empty
                String valStr = (String)value;
                Assert.assertFalse(valStr.trim().isEmpty(), field+" was empty");
            }
        }
    }
    
    public static boolean isValidPositiveNumber(String numStr) {
        int num;

        try {
            num = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            Assert.fail("Not a valid number");
            return false;
        }

        return num >= 0;
    }

    public static boolean isValidDateFormat(String value, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        try {
            LocalDate.parse(value, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}