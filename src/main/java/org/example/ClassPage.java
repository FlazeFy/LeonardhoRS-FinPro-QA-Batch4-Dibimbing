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

    // Input Element - Announcement
    @FindBy(id = "create-announcement-title-input")
    private WebElement announcementTitleInput;

    @FindBy(id = "edit-announcement-title-input")
    private WebElement announcementTitleEditInput;

    @FindBy(id = "delete-announcement-delete-button")
    private WebElement announcementDeleteButton;

    // Input Element - Content
    @FindBy(xpath = "//input[@placeholder='Search content...']")
    private WebElement searchClassContentInput;

    @FindBy(xpath = "//input[@placeholder='Title']")
    private WebElement contentTitleInput;

    @FindBy(xpath = "//input[@placeholder='Check In Key']")
    private WebElement contentCheckInKeyInput;

    @FindBy(xpath = "//input[@placeholder='Check Out Key']")
    private WebElement contentCheckOutKeyInput;

    @FindBy(xpath = "//input[@placeholder='Link Meeting Class']")
    private WebElement contentLinkMeetingClassInput;

    @FindBy(xpath = "//input[@placeholder='Pre Test Url']")
    private WebElement contentPreTestUrlInput;

    @FindBy(xpath = "//input[@placeholder='Live Class Duration']")
    private WebElement contentLiveClassDurationInput;

    @FindBy(xpath = "//input[@placeholder='Live Class Date']")
    private WebElement contentLiveClassDateInput;

    // Richtext Editor Element
    @FindBy(id = "create-announcement-description-input")
    private WebElement announcementDescInput;

    @FindBy(id = "edit-announcement-description-input")
    private WebElement announcementDescEditInput;

    // Select Element
    @FindBy(xpath = "//button[.//p[normalize-space()='Filter by Angkatan']]")
    private WebElement filterClassBatchSelect;

    // Button Element
    @FindBy(xpath = "//button[normalize-space()='Add New Class']")
    private WebElement addNewClassButton;

    @FindBy(id = "create-announcement-button")
    private WebElement submitAnnouncementButton;

    @FindBy(id = "edit-announcement-button")
    private WebElement submitEditAnnouncementButton;

    @FindBy(id = "content-create-button")
    private WebElement addNewContentButton;

    @FindBy(xpath = "//button[normalize-space()='Add Content']")
    private WebElement submitContentButton;

    // Text
    @FindBy(xpath = "//p[normalize-space()='Manage Class']")
    private WebElement classManagementSectionTitle;

    @FindBy(xpath = "//p[normalize-space()='Announcement']")
    private WebElement classAnnouncementSectionTitle;

    @FindBy(xpath = "//p[normalize-space()='Content']")
    private WebElement classContentSectionTitle;

    @FindBy(xpath = "//section[contains(@class,'chakra-modal__content') and @role='dialog']//header//p[normalize-space()='Delete Announcement']")
    private WebElement deleteAnnouncementModalTitle;

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

    public boolean isClassContentSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classContentSectionTitle);
            return classContentSectionTitle.isDisplayed();
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
            waitForElementToBeVisible(addNewClassButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[normalize-space()='Add New Class']/following::div[contains(., '" + message + "')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isClassContentFailedMessageDisplayed(String message) {
        try {
            waitForElementToBeVisible(addNewContentButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[normalize-space()='Create Content']/following::div[contains(., '" + message + "')]")
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

    // Select Announcement
    private List<WebElement> getAnnouncementCards() {
        waitForElementToBeVisible(classAnnouncementSectionTitle);

        wait.until(driver ->
                !driver.findElements(By.xpath("//li[contains(@class,'chakra-stack')][.//h2]")).isEmpty()
        );

        return driver.findElements(By.xpath("//li[contains(@class,'chakra-stack')][.//h2]"));
    }

    public boolean isAnnouncementListDisplayed() {
        try {
            List<WebElement> data = getAnnouncementCards();
            if (data.isEmpty()) return false;

            int idx = 0;
            for (WebElement dt : data) {
                // Announcement title
                boolean hasTitle = !dt.findElements(By.xpath(".//h2[normalize-space()]")).isEmpty();

                // Role + Created At (first p)
                boolean hasRoleAndCreatedAt = !dt.findElements(By.xpath(".//h2/following::p[1][normalize-space()]")).isEmpty();

                // Description (last p)
                boolean hasDescription = !dt.findElements(By.xpath(".//p[last()][normalize-space()]")).isEmpty();

                // Edit button
                boolean hasEditButton = !dt.findElements(By.xpath(".//button[normalize-space()='Edit']")).isEmpty();

                // Delete button
                boolean hasDeleteButton = !dt.findElements(By.xpath(".//button[normalize-space()='Delete']")).isEmpty();

                if (!(hasTitle && hasRoleAndCreatedAt && hasDescription && hasEditButton && hasDeleteButton)) {
                    System.out.println("Announcement invalid at - " + idx);
                    return false;
                }

                idx++;
            }

            return true;
        } catch (Exception e) {
            logger.error("Announcement list validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    public List<Map<String, String>> getAnnouncementCardData() {
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement dt : getAnnouncementCards()) {
            Map<String, String> data = new HashMap<>();

            String roleAndCreatedAt = dt.findElement(By.xpath(".//h2/following::p[1][normalize-space()]")).getText().trim();
            String[] parts = roleAndCreatedAt.split(",\\s*", 2);
            data.put("title", dt.findElement(By.xpath(".//h2[normalize-space()]")).getText().trim());
            data.put("role", parts.length > 0 ? parts[0].trim() : "");
            data.put("created-at", parts.length > 1 ? parts[1].trim() : "");
            data.put("description", dt.findElement(By.xpath(".//p[last()][normalize-space()]")).getText().trim());

            result.add(data);
        }

        return result;
    }

    // Select Content
    private List<WebElement> getContentCards() {
        waitForElementToBeVisible(classContentSectionTitle);

        By contentCards = By.xpath(
                "//button[normalize-space()='Create Content']/following::div[contains(@class,'chakra-stack')][1]" +
                        "//div[contains(@class,'chakra-accordion__item')]"
        );

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(contentCards));

        return driver.findElements(contentCards);
    }

    public boolean isContentListDisplayed() {
        try {
            List<WebElement> data = getContentCards();
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

    public List<Map<String, String>> getContentCardData() {
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement dt : getContentCards()) {
            Map<String, String> data = new HashMap<>();

            data.put("title", dt.findElement(By.xpath(".//button[1]//p[1]")).getText().trim());
            data.put("created-at", dt.findElement(By.xpath(".//button[1]//p[2]")).getText().trim());

            result.add(data);
        }

        return result;
    }

    // For Assertion
    public boolean isSearchClassDisplayed() {
        try {
            waitForElementToBeVisible(searchClassInput);
            return searchClassInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSearchClassContentDisplayed() {
        try {
            waitForElementToBeVisible(searchClassContentInput);
            return searchClassContentInput.isDisplayed();
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

    public boolean isDeleteAnnouncementModalDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(deleteAnnouncementModalTitle));
            return deleteAnnouncementModalTitle.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    // Action
    public void fillSearchClass(String classTitle) {
        searchClassInput.sendKeys(classTitle);
    }

    public void fillSearchClassContent(String classContentTitle) {
        searchClassContentInput.sendKeys(classContentTitle);
    }

    public void fillCreateAnnouncement(String announcementTitle, String announcementDesc) {
        waitForElementToBeVisible(announcementTitleInput);
        announcementTitleInput.sendKeys(announcementTitle);
        waitForElementToBeVisible(announcementDescInput);
        announcementDescInput.sendKeys(announcementDesc);
    }

    public void fillEditAnnouncement(String announcementTitle, String announcementDesc) {
        waitForElementToBeVisible(announcementTitleEditInput);
        clearInput(announcementTitleEditInput);
        announcementTitleEditInput.sendKeys(announcementTitle);

        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-announcement-description-input")));
        clearRichText(editor);
        editor.sendKeys(announcementDesc);
    }

    public void fillCreateContent(
            String contentTitle, String contentDesc, String checkInKey, String checkOutKey, String contentPreTestUrl,
            String contentLinkMeeting, String liveClassDuration, String liveClassDate) {
        // Content Title
        waitForElementToBeVisible(contentTitleInput);
        contentTitleInput.sendKeys(contentTitle);
        // Content Desc
        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[normalize-space()='Description']/following::div[@role='textbox'][1]")));
        editor.sendKeys(contentDesc);
        // Content Check In Key
        waitForElementToBeVisible(contentCheckInKeyInput);
        contentCheckInKeyInput.sendKeys(checkInKey);
        // Content Check Out Key
        waitForElementToBeVisible(contentCheckOutKeyInput);
        contentCheckOutKeyInput.sendKeys(checkOutKey);
        // Enable Check In & Check Out
        selectCheckInOutKeyOption("Enable", "Check In");
        selectCheckInOutKeyOption("Enable", "Check Out");
        // Content Link Meeting Class
        waitForElementToBeVisible(contentLinkMeetingClassInput);
        contentLinkMeetingClassInput.sendKeys(contentLinkMeeting);
        // Content Pre Test URL
        waitForElementToBeVisible(contentPreTestUrlInput);
        contentPreTestUrlInput.sendKeys(contentPreTestUrl);
        // Content Live Class Duration
        waitForElementToBeVisible(contentLiveClassDurationInput);
        contentLiveClassDurationInput.sendKeys(liveClassDuration);
        // Content Live Class Date
        waitForElementToBeVisible(contentLiveClassDateInput);
        contentLiveClassDateInput.sendKeys(liveClassDate);
    }

    public void clickSubmitAnnouncement() {
        submitAnnouncementButton.click();
    }

    public void clickSubmitEditAnnouncement() {
        submitEditAnnouncementButton.click();
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

    public boolean findAnnouncementByTitle(String title) {
        List<WebElement> cards = getAnnouncementCards();

        for (WebElement card : cards) {
            String announcementTitle = card.findElement(By.xpath(".//h2")).getText().trim();
            if (announcementTitle.equalsIgnoreCase(title.trim())) return true;
        }

        return false;
    }

    public void clickAnnouncementButton(String title, String type) {
        List<WebElement> cards = getAnnouncementCards();

        for (WebElement card : cards) {
            String announcementTitle = card.findElement(By.xpath(".//h2")).getText().trim();

            if (announcementTitle.equalsIgnoreCase(title.trim())) {
                WebElement editButton = card.findElement(By.xpath(".//button[normalize-space()='" + type + "']"));
                wait.until(ExpectedConditions.elementToBeClickable(editButton));
                editButton.click();
                return;
            }
        }

        throw new NoSuchElementException("No announcement found with title: " + title);
    }

    public void clickEditAnnouncementByTitle(String title) {
        clickAnnouncementButton(title, "Edit");
    }

    public void clickDeleteAnnouncementByTitle(String title) {
        clickAnnouncementButton(title, "Delete");
    }

    public void clickSubmitDeleteAnnouncement() {
        announcementDeleteButton.click();
    }

    public void clickAddContentPageButton() {
        waitForElementToBeVisible(addNewContentButton);
        addNewContentButton.click();
    }

    public void clickSubmitContent() {
        submitContentButton.click();
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

    public void setAttendanceEnabled(boolean enabled) {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//p[normalize-space()='Activate Attendance']/following::input[@type='checkbox'][1]")
        ));

        if (checkbox.isSelected() != enabled) {
            WebElement toggle = driver.findElement(
                    By.xpath("//p[normalize-space()='Activate Attendance']/following::span[contains(@class,'chakra-switch__track')][1]")
            );

            wait.until(ExpectedConditions.elementToBeClickable(toggle));
            toggle.click();

            wait.until(driver -> checkbox.isSelected() == enabled);
        }
    }

    public void selectCheckInOutKeyOption(String option, String type) {
        WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                    "//p[normalize-space()='Enable " + type + " Key']/following::div[@role='radiogroup'][1]" +
                            "//p[normalize-space()='" + option + "']"
            )
        ));

        radio.click();
    }

    public void waitForPageLoading() {
        wait.until(driver ->
                driver.findElements(By.cssSelector("#nprogress")).isEmpty()
        );
    }
}
