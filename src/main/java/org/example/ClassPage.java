package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    // Button Element
    @FindBy(xpath = "//button[normalize-space()='Add New Class']")
    private WebElement addNewClassButton;

    // Text
    @FindBy(xpath = "//p[normalize-space()='Manage Class']")
    private WebElement classManagementSectionTitle;

    public ClassPage(WebDriver driver) {
        super(driver);
    }

    // Class Management
    public boolean isClassManagementSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classManagementSectionTitle);
            return classManagementSectionTitle.isDisplayed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Class Item
    public List<Map<String, String>> getClassCardData() {
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement card : getClassCards()) {
            Map<String, String> data = new HashMap<>();

            data.put("title", card.findElement(By.xpath(".//p[1]")).getText().trim());
            data.put("major", card.findElement(By.xpath(".//p[contains(.,'Program Studi:')]"))
                    .getText().replace("Program Studi:", "").trim());
            data.put("batch", card.findElement(By.xpath(".//p[contains(.,'Angkatan:')]"))
                    .getText().replace("Angkatan:", "").trim());
            data.put("employee", card.findElement(By.xpath(".//p[contains(.,'Employee join this class')]"))
                    .getText().replace("Employee join this class", "").trim());
            data.put("startDate", card.findElement(By.xpath(".//p[contains(.,'Started at')]"))
                    .getText().replace("Started at", "").trim());

            result.add(data);
        }

        return result;
    }

    private List<WebElement> getClassCards() {
        wait.until(ExpectedConditions.visibilityOf(addNewClassButton));

        // wait for the skeleton loading state to clear before reading real content
        wait.until(driver -> {
            List<WebElement> images = driver.findElements(By.cssSelector("img[alt='bootcamp image']"));
            return !images.isEmpty();
        });

        return driver.findElements(
                By.xpath("//button[normalize-space()='Add New Class']/following::img[@alt='bootcamp image']/ancestor::div[3]")
        );
    }

    public boolean isClassListDisplayed() {
        try {
            List<WebElement> cards = getClassCards();
            if (cards.isEmpty()) return false;

            int idx = 0;
            for (WebElement card : cards) {
                // validate class image
                boolean hasImage = !card.findElements(By.cssSelector("img[alt='bootcamp image']")).isEmpty();

                // validate class title
                boolean hasTitle = !card.findElements(By.xpath(".//p[1]")).isEmpty();

                // validate edit and manage content button
                boolean hasEditButton = !card.findElements(By.xpath(".//button[contains(text(),'Edit & Manage Content')]")).isEmpty();

                // validate delete class button
                boolean hasDeleteButton = !card.findElements(By.xpath(".//button[contains(text(),'Delete Class')]")).isEmpty();

                // validate program studi info
                boolean hasMajor = !card.findElements(By.xpath(".//p[contains(text(),'Program Studi:')]")).isEmpty();

                // validate angkatan info
                boolean hasBatch = !card.findElements(By.xpath(".//p[contains(text(),'Angkatan:')]")).isEmpty();

                // validate employee join info
                // boolean hasEmployeeJoin = !card.findElements(By.xpath(".//p[contains(text(),'Employee join this class')]")).isEmpty();
                boolean hasEmployeeJoin = !card.findElements(By.xpath(".//p[contains(.,'Employee join this class')]")).isEmpty();

                // validate class start date info
                boolean hasStartDate = !card.findElements(By.xpath(".//p[contains(text(),'Started at')]")).isEmpty();

                if (!(hasImage && hasTitle && hasEditButton && hasDeleteButton && hasMajor && hasBatch && hasEmployeeJoin && hasStartDate)) {
                    System.out.println("Class invalid at - "+idx);
                    return false;
                }

                idx++;
            }

            return true;
        } catch (Exception e) {
            logger.error("Class list validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}
