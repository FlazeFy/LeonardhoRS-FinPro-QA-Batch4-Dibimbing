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

    // Input Element
    @FindBy(xpath = "//input[@placeholder='Search class...']")
    private WebElement searchClassInput;

    @FindBy(id = "create-announcement-title-input")
    private WebElement announcementTitleInput;

    // Richtext Editor Element
    @FindBy(id = "create-announcement-description-input")
    private WebElement announcementDescInput;

    // Select Element
    @FindBy(xpath = "//button[.//p[normalize-space()='Filter by Angkatan']]")
    private WebElement filterClassBatchSelect;

    // Button Element
    @FindBy(xpath = "//button[normalize-space()='Add New Class']")
    private WebElement addNewClassButton;

    @FindBy(id = "create-announcement-button")
    private WebElement submitAnnouncementButton;

    // Text
    @FindBy(xpath = "//p[normalize-space()='Manage Class']")
    private WebElement classManagementSectionTitle;

    @FindBy(xpath = "//p[normalize-space()='Announcement']")
    private WebElement classAnnouncementSectionTitle;

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

    public boolean isClassAnnouncementSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classAnnouncementSectionTitle);
            return classAnnouncementSectionTitle.isDisplayed();
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

    public void openClassDetail(String title) {
        List<WebElement> cards = getClassCards();

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

    public boolean isClassFailedMessageDisplayed(String message) {
        try {
            wait.until(ExpectedConditions.visibilityOf(addNewClassButton));

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[normalize-space()='Add New Class']/following::div[contains(., '" + message + "')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isClassListDisplayed() {
        try {
            List<WebElement> data = getClassCards();
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

    // Assertion
    public boolean isSearchClassDisplayed() {
        try {
            waitForElementToBeVisible(searchClassInput);
            return searchClassInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFilterClassBatchDisplayed() {
        try {
            waitForElementToBeVisible(filterClassBatchSelect);
            return filterClassBatchSelect.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Action
    public void fillSearchClass(String classTitle) {
        searchClassInput.sendKeys(classTitle);
    }

    public void fillCreateAnnouncement(String announcementTitle, String announcementDesc) {
        waitForElementToBeVisible(announcementTitleInput);
        announcementTitleInput.sendKeys(announcementTitle);
        waitForElementToBeVisible(announcementDescInput);
        announcementDescInput.sendKeys(announcementDesc);
    }

    public void clickSubmitAnnouncement() {
        submitAnnouncementButton.click();
    }

    public void openTabByTitle(String title) {
        try {
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@role='tablist']//button[@role='tab'][normalize-space()='" + title + "']")
            ));

            tab.click();

            // Wait until the clicked tab becomes active
            wait.until(ExpectedConditions.attributeToBe(tab, "aria-selected", "true"));
        } catch (TimeoutException e) {
            throw new NoSuchElementException("Tab not found or could not be opened: " + title, e);
        }
    }

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
}
