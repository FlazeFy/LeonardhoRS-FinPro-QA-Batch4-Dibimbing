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

public class ContentSection extends BasePage {
    private static final Logger logger = LogManager.getLogger(ContentSection.class);

    // Input Element
    @FindBy(xpath = "//input[@placeholder='Search content...']")
    private WebElement searchClassContentInput;

    @FindBy(xpath = "//input[@placeholder='Live Class Date']")
    private WebElement contentLiveClassDateInput;

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

    // Button Element
    @FindBy(id = "content-create-button")
    private WebElement addNewContentButton;

    @FindBy(xpath = "//button[normalize-space()='Add Content']")
    private WebElement submitContentButton;

    @FindBy(xpath = "//button[normalize-space()='Update Content']")
    private WebElement submitUpdateContentButton;

    // Text Element
    @FindBy(xpath = "//p[normalize-space()='Content']")
    private WebElement classContentSectionTitle;

    @FindBy(xpath = "//p[normalize-space()='Update Content']")
    private WebElement classUpdateContentSectionTitle;

    public ContentSection(WebDriver driver) {
        super(driver);
    }

    // Select Action
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

    private void selectCheckInOutKeyOption(String option, String type) {
        WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(
                        "//p[normalize-space()='Enable " + type + " Key']/following::div[@role='radiogroup'][1]" +
                                "//p[normalize-space()='" + option + "']"
                )
        ));

        radio.click();
    }

    public void toggleHide(String contentTitle, String type) {
        WebElement expandedAccordion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'chakra-accordion__item')][.//button[@aria-expanded='true']]")
                )
        );

        String currentTitle = expandedAccordion.findElement(By.xpath(".//button//p[1]")).getText().trim();

        if (!currentTitle.equalsIgnoreCase(contentTitle.trim())) {
            throw new NoSuchElementException(
                    "Expanded content title mismatch. Expected: '" + contentTitle + "', but found: '" + currentTitle + "'"
            );
        }

        WebElement toggleButton = expandedAccordion.findElement(By.xpath(".//button[normalize-space()='" + type + "']"));

        // Scroll button into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});",  toggleButton);

        wait.until(ExpectedConditions.elementToBeClickable(toggleButton));
        toggleButton.click();
    }

    // Fill Input Action
    public void fillSearch(String classContentTitle) {
        waitForElementToBeVisible(searchClassContentInput);
        searchClassContentInput.sendKeys(classContentTitle);
    }

    public void fillCreate(
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

    public void fillEdit(
            String contentTitle, String contentDesc, String checkInKey, String checkOutKey, String contentPreTestUrl,
            String contentLinkMeeting, String liveClassDuration, String liveClassDate) {

        clearAndFill(contentTitleInput, contentTitle);
        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='Description']/following::div[@role='textbox'][1]")));
        clearRichText(editor);
        editor.sendKeys(contentDesc);
        clearAndFill(contentCheckInKeyInput, checkInKey);
        clearAndFill(contentCheckOutKeyInput, checkOutKey);
        selectCheckInOutKeyOption("Enable", "Check In");
        selectCheckInOutKeyOption("Enable", "Check Out");
        clearAndFill(contentLinkMeetingClassInput, contentLinkMeeting);
        clearAndFill(contentPreTestUrlInput, contentPreTestUrl);
        clearAndFill(contentLiveClassDurationInput, liveClassDuration);
        clearAndFill(contentLiveClassDateInput, liveClassDate);
    }

    // Visibility Action
    public boolean isSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classContentSectionTitle);
            return classContentSectionTitle.isDisplayed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isUpdateSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classUpdateContentSectionTitle);
            return classUpdateContentSectionTitle.isDisplayed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isSearchDisplayed() {
        try {
            waitForElementToBeVisible(searchClassContentInput);
            return searchClassContentInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isToggleButtonDisplayed(String contentTitle, String type) {
        WebElement expandedAccordion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'chakra-accordion__item')][.//button[@aria-expanded='true']]")
                )
        );

        String currentTitle = expandedAccordion.findElement(By.xpath(".//button//p[1]")).getText().trim();

        if (!currentTitle.equalsIgnoreCase(contentTitle.trim())) {
            throw new NoSuchElementException("Expanded content title mismatch. Expected: '" + contentTitle + "', but found: '" + currentTitle + "'");
        }

        return !expandedAccordion.findElements(By.xpath(".//button[normalize-space()='" + type + "']")).isEmpty();
    }

    public boolean isFailedMessageDisplayed(String message) {
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

    // Click Action
    public void clickAddButton() {
        waitForElementToBeVisible(addNewContentButton);
        addNewContentButton.click();
    }

    public void clickSubmit() {
        submitContentButton.click();
    }

    public void clickSubmitUpdate () {
        submitUpdateContentButton.click();
    }

    public void clickAnItem(String contentTitle) {
        List<WebElement> cards = getElement();

        for (WebElement card : cards) {
            String title = card.findElement(By.xpath(".//button//p[1]")).getText().trim();
            if (title.equalsIgnoreCase(contentTitle.trim())) {
                WebElement accordionButton = card.findElement(By.xpath(".//button"));
                wait.until(ExpectedConditions.elementToBeClickable(accordionButton));
                accordionButton.click();

                // Wait until accordion is expanded
                wait.until(ExpectedConditions.attributeToBe(accordionButton, "aria-expanded", "true"));
                return;
            }
        }

        throw new NoSuchElementException("No content found with title: " + contentTitle);
    }

    private void clickContentButton(String contentTitle, String type) {
        WebElement expandedAccordion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'chakra-accordion__item')][.//button[@aria-expanded='true']]")
                )
        );

        String currentTitle = expandedAccordion.findElement(By.xpath(".//button//p[1]")).getText().trim();
        if (!currentTitle.equalsIgnoreCase(contentTitle.trim())) {
            throw new NoSuchElementException("Expanded content title mismatch. Expected: '" + contentTitle + "', but found: '" + currentTitle + "'");
        }

        WebElement actionButton = expandedAccordion.findElement(By.xpath(".//button[normalize-space()='" + type + "']"));

        wait.until(ExpectedConditions.elementToBeClickable(actionButton));
        actionButton.click();
    }

    public void deleteAnItem(String contentTitle) {
        clickContentButton(contentTitle, "Delete");
    }

    public void openEdit(String contentTitle) {
        clickContentButton(contentTitle, "Edit Content");
    }

    // Validate List Action
    private List<WebElement> getElement() {
        waitForElementToBeVisible(classContentSectionTitle);

        By contentCards = By.xpath(
                "//button[normalize-space()='Create Content']/following::div[contains(@class,'chakra-stack')][1]" +
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
