package org.example.section.classmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSection extends BasePage {
    private static final Logger logger = LogManager.getLogger(TestSection.class);

    @FindBy(xpath = "//p[normalize-space()='List Test']")
    public WebElement classTestSectionTitle;

    public TestSection(WebDriver driver) {
        super(driver);
    }

    // Visibility Action
    public boolean isSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classTestSectionTitle);
            return classTestSectionTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Validate List Action
    private List<WebElement> getElement() {
        waitForElementToBeVisible(classTestSectionTitle);

        By contentCards = By.xpath(
                "//button[@id='create-class-test-button']/following::div[contains(@class,'chakra-stack')][1]" +
                        "//div[contains(@class,'chakra-accordion__item')]"
        );

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(contentCards));

        return driver.findElements(contentCards);
    }

    public boolean isListDisplayed() {
        try {
            List<WebElement> data = getElement();
            if (data.isEmpty()) return false;

            int idx = 0;
            for (WebElement dt : data) {
                System.out.println(dt);
                // Content Title (first p)
                boolean hasTitle = !dt.findElements(By.xpath(".//button[1]//p[1]")).isEmpty();
                // Live Class Date (second p)
                boolean hasDate = !dt.findElements(By.xpath(".//button[1]//p[2]")).isEmpty();

                if (!(hasTitle && hasDate)) {
                    System.out.println("Content invalid at - " + idx);
                    return false;
                }

                idx++;
            }

            return true;
        } catch (Exception e) {
            logger.error("Content list validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    public List<Map<String, String>> getData() {
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement dt : getElement()) {
            Map<String, String> data = new HashMap<>();

            data.put("title", dt.findElement(By.xpath(".//button[1]//p[1]")).getText().trim());
            data.put("created-at", dt.findElement(By.xpath(".//button[1]//p[2]")).getText().trim());

            result.add(data);
        }

        return result;
    }
}
