package core;

import io.restassured.specification.RequestSpecification;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class TestUtil extends BaseApiTest{

    public static Object[][] getTestData(String filePath, String sheetName) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Sheet sheet = workbook.getSheet(sheetName);
        int rows = sheet.getPhysicalNumberOfRows();
        int cols = sheet.getRow(0).getPhysicalNumberOfCells();

        Object[][] data = new Object[rows - 1][cols]; // skip header row

        for (int i = 1; i < rows; i++) { // start from 1 to skip header
            Row row = sheet.getRow(i);
            for (int j = 0; j < cols; j++) {
                Cell cell = row.getCell(j);
                data[i - 1][j] = getCellValue(cell);
            }
        }

        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> (cell.getNumericCellValue() % 1 == 0)
                    ? (int) cell.getNumericCellValue()
                    : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> "";
        };
    }

    // API Test
    public static Response templateGraphQLRequest(String endpointName, String graphqlQuery, Object variables, String username, String password, String sessionId) {
        String contentType = "application/json";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", graphqlQuery);
        if (variables != null) requestBody.put("variables", variables);

        RequestSpecification request = given().contentType(contentType);
        // Basic Auth
        if (username != null && !username.isBlank()) {
            request.auth().preemptive().basic(username, password);
        }
        // Session Cookie
        if (sessionId != null && !sessionId.isBlank()) {
            request.cookie("sid_b2b", sessionId);
        }

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

    public static String getSid() {
        final String mutation = """
            mutation Login(
               $companyId: String!
               $usernameOrEmail: String!
               $password: String!
             ) {
               login(
                 companyId: $companyId
                 usernameOrEmail: $usernameOrEmail
                 password: $password
               ) {
                 user {
                   id
                 }
                 errors {
                   field
                   message
                 }
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
}