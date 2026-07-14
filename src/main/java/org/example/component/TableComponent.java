package org.example.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableComponent extends BasePage {
    private static final Logger logger = LogManager.getLogger(TableComponent.class);

    public TableComponent(WebDriver driver) {
        super(driver);
    }

    public WebElement getElement(WebElement afterElement) {
//        waitForElementToBeVisible(classManagementSectionTitle);

        if (afterElement != null) {
            waitForElementToBeVisible(afterElement);
            return afterElement.findElement(
                    By.xpath("ancestor::section[1]//table")
            );
        }

        wait.until(driver -> !driver.findElements(By.xpath("//table[.//th]")).isEmpty());

        return driver.findElement(By.xpath("//table[.//th]"));
    }

    public boolean isDisplayed(WebElement afterElement) {
        try {
            WebElement table = getElement(afterElement);

            boolean hasThead = !table.findElements(By.tagName("thead")).isEmpty();
            boolean hasTbody = !table.findElements(By.tagName("tbody")).isEmpty();

            return hasThead && hasTbody;
        } catch (Exception e) {
            logger.error("Table display check failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    public boolean isDataValid(WebElement afterElement, String tagName) {
        try {
            WebElement table = getElement(afterElement);
            // check this, still failed at the TC-CLMG-037
            wait.until(driver -> {
                List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr"));
                return rows.stream()
                        .anyMatch(row -> !row.getText().trim().isEmpty());
            });
            List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr"));
            if (rows.isEmpty()) return false;

            int idx = 0;
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.isEmpty()) return false;

                // Validate every column (except the last) is not empty
                boolean hasAllColumnsFilled = true;
                for (int i = 0; i < cells.size() - 1; i++) {
                    if (cells.get(i).getText().trim().isEmpty()) {
                        hasAllColumnsFilled = false;
                        break;
                    }
                }

                // Validate the last column contains a custom search
                WebElement lastCell = cells.get(cells.size() - 1);
                boolean hasActionButton = wait.until(
                        ExpectedConditions.presenceOfNestedElementLocatedBy(lastCell, By.tagName(tagName))
                ) != null;

                if (!(hasAllColumnsFilled && hasActionButton)) {
                    System.out.println("Table row invalid at - " + idx);
                    return false;
                }

                idx++;
            }

            return true;
        } catch (Exception e) {
            logger.error("Table data validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    public List<Map<String, String>> getData(WebElement afterElement) {
        List<Map<String, String>> result = new ArrayList<>();

        WebElement table = getElement(afterElement);
        List<WebElement> headers = table.findElements(By.xpath(".//thead//th"));
        List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            Map<String, String> data = new HashMap<>();

            for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                String key = headers.get(i).getText().trim();
                String value = cells.get(i).getText().trim();
                data.put(key, value);
            }

            result.add(data);
        }

        return result;
    }
}
