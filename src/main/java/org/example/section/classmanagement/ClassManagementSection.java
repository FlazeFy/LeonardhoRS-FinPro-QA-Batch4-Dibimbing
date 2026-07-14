package org.example.section.classmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassManagementSection extends BasePage {
    private static final Logger logger = LogManager.getLogger(ClassManagementSection.class);

    // Input Element
    @FindBy(xpath = "//input[@placeholder='Search class...']")
    private WebElement searchClassInput;

    // Select Element
    @FindBy(xpath = "//button[.//p[normalize-space()='Filter by Angkatan']]")
    private WebElement filterClassBatchSelect;

    // Button Element
    @FindBy(xpath = "//button[normalize-space()='Add New Class']")
    private WebElement addNewClassButton;

    public ClassManagementSection(WebDriver driver) {
        super(driver);
    }

    // Select Action
    public void selectClassBatch(String batch) {
        try {
            filterClassBatchSelect.click();
            WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@role='menu']//button[@role='menuitem'][normalize-space(.)='" + batch + "']")
            ));
            menuItem.click();

            // Confirm the dropdown menu is hidden (render finish)
            wait.until(driver -> {
                WebElement menu = driver.findElement(By.xpath(
                        "//button[.//p[normalize-space()='Filter by Angkatan']]/following::div[@role='menu'][1]"
                ));

                String visibility = menu.getCssValue("visibility");
                return "hidden".equalsIgnoreCase(visibility);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Visibility Action
    public boolean isSearchDisplayed() {
        try {
            waitForElementToBeVisible(searchClassInput);
            return searchClassInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFailedMessageDisplayed(String message) {
        try {
            waitForElementToBeVisible(addNewClassButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[normalize-space()='Add New Class']/following::div[contains(., '" + message + "')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isFilterBatchDisplayed() {
        try {
            waitForElementToBeVisible(filterClassBatchSelect);
            return filterClassBatchSelect.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Fill Input Action
    public void fillSearch(String classTitle) {
        searchClassInput.sendKeys(classTitle);
    }

    // Navigate Action
    public void openDetail(String title) {
        List<WebElement> cards = getElement();

        for (WebElement card : cards) {
            String cardTitle = card.findElement(By.xpath(".//p[1]")).getText().trim().toLowerCase();

            if (cardTitle.equals(title.trim().toLowerCase())) {
                WebElement editButton = card.findElement(By.xpath(".//button[normalize-space()='Edit & Manage Content']"));
                wait.until(ExpectedConditions.elementToBeClickable(editButton));
                editButton.click();
                return;
            }
        }

        throw new NoSuchElementException("No class card found with title: " + title);
    }

    // Validate List Item
    private List<WebElement> getElement() {
        waitForElementToBeVisible(addNewClassButton);

        // wait for the skeleton loading state to clear before reading real content
        wait.until(driver -> {
            List<WebElement> images = driver.findElements(By.cssSelector("img[alt='bootcamp image']"));
            return !images.isEmpty();
        });

        return driver.findElements(
                By.xpath("//button[normalize-space()='Add New Class']/following::img[@alt='bootcamp image']/ancestor::div[3]")
        );
    }

    public List<Map<String, String>> getData() {
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement card : getElement()) {
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

    public boolean isListDisplayed() {
        try {
            List<WebElement> data = getElement();
            if (data.isEmpty()) return false;

            int idx = 0;
            for (WebElement dt : data) {
                // validate class image
                boolean hasImage = !dt.findElements(By.cssSelector("img[alt='bootcamp image']")).isEmpty();

                // validate class title
                boolean hasTitle = !dt.findElements(By.xpath(".//p[1]")).isEmpty();

                // validate edit and manage content button
                boolean hasEditButton = !dt.findElements(By.xpath(".//button[contains(text(),'Edit & Manage Content')]")).isEmpty();

                // validate delete class button
                boolean hasDeleteButton = !dt.findElements(By.xpath(".//button[contains(text(),'Delete Class')]")).isEmpty();

                // validate program studi info
                boolean hasMajor = !dt.findElements(By.xpath(".//p[contains(text(),'Program Studi:')]")).isEmpty();

                // validate angkatan info
                boolean hasBatch = !dt.findElements(By.xpath(".//p[contains(text(),'Angkatan:')]")).isEmpty();

                // validate employee join info
                // boolean hasEmployeeJoin = !card.findElements(By.xpath(".//p[contains(text(),'Employee join this class')]")).isEmpty();
                boolean hasEmployeeJoin = !dt.findElements(By.xpath(".//p[contains(.,'Employee join this class')]")).isEmpty();

                // validate class start date info
                boolean hasStartDate = !dt.findElements(By.xpath(".//p[contains(text(),'Started at')]")).isEmpty();

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
