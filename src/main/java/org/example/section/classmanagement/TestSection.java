package org.example.section.classmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class TestSection extends BasePage {
    private static final Logger logger = LogManager.getLogger(TestSection.class);

    // Input Element
    @FindBy(xpath = "//input[@placeholder='Search test...']")
    private WebElement classTestSearchInput;

    @FindBy(id = "create-class-test-title-input")
    private WebElement testTitleInput;

    @FindBy(id = "create-class-test-start-date-input")
    private WebElement testStartDateInput;

    @FindBy(id = "create-class-test-duration-date-input")
    private WebElement testDeadlineDateInput;

    @FindBy(id = "create-class-test-test-type-input")
    private WebElement testTypeInput;

    @FindBy(id = "create-class-test-duration-timer-input")
    private WebElement testDurationInput;

    @FindBy(id = "create-class-test-choose-mentor-input")
    private WebElement testMentorNameInput;

    @FindBy(id = "create-class-test-content-id-input")
    private WebElement testClassContentNameInput;

    // Radio Element
    @FindBy(id = "create-class-test-test-relation-input")
    private WebElement testContentTypeRadio;

    @FindBy(id = "create-class-test-duration-method-input")
    private WebElement testDurationMethodRadio;

    @FindBy(id = "create-class-test-need-finish-grading-input")
    private WebElement testNeedGradingStatusRadio;

    // Text Element
    @FindBy(xpath = "//p[normalize-space()='List Test']")
    public WebElement classTestSectionTitle;

    // Button Element
    @FindBy(id = "create-class-test-button")
    private WebElement addTestButton;

    @FindBy(id = "create-class-test-create-test-button")
    private WebElement submitTestButton;

    public TestSection(WebDriver driver) {
        super(driver);
    }

    // Click Action
    public void clickAddButton() {
        waitForElementToBeVisible(addTestButton);
        addTestButton.click();
    }

    public void clickSubmit() {
        waitForElementToBeVisible(submitTestButton);
        submitTestButton.click();
    }

    // Fill Input Action
    public void fillSearchAdd(String mentorName) {
        classTestSearchInput.sendKeys(mentorName);
    }

    // If content test type not equal to 'Class Test'. Test title act as content title
    public void fillCreate(
        String testTitle, String testType, String testDurationMethod, String duration, String mentorName, String startDate,
        String contentTestType, String deadlineDate
    ) {
        if (contentTestType.equals("Class Test")) {
            // Test Title
            waitForElementToBeVisible(testTitleInput);
            testTitleInput.sendKeys(testTitle);

            // Start Date
            waitForElementToBeVisible(testStartDateInput);
            testStartDateInput.sendKeys(startDate);
        } else {
            // Content Title
            waitForElementToBeVisible(testClassContentNameInput);
            testClassContentNameInput.sendKeys(testTitle);
        }

        // Test Type
        waitForElementToBeVisible(testTypeInput);
        testTypeInput.sendKeys(testType);

        // Test Duration Method
        selectRadioGroup(testDurationMethodRadio, testDurationMethod);
        if (testDurationMethod.equals("Deadline")) {
            // Deadline Date
            waitForElementToBeVisible(testDeadlineDateInput);
            testDeadlineDateInput.sendKeys(deadlineDate);
        } else {
            // Test Duration
            waitForElementToBeVisible(testDurationInput);
            testDurationInput.sendKeys(duration);
        }

        // Test Grading Status
        selectRadioGroup(testNeedGradingStatusRadio, "No");
        // Mentor Name
        waitForElementToBeVisible(testMentorNameInput);
        testMentorNameInput.sendKeys(mentorName);
    }

    private void selectRadioGroup(WebElement radioElement, String val) {
        waitForElementToBeVisible(radioElement);

        WebElement radioOption = radioElement.findElement(
                By.xpath(".//label[.//p[starts-with(normalize-space(), '" + val + "')]]")
        );

        wait.until(ExpectedConditions.elementToBeClickable(radioOption));
        radioOption.click();
    }

    public void setTestContentType(String testContentType) {
        selectRadioGroup(testContentTypeRadio, testContentType);
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

    public boolean isSearchDisplayed() {
        try {
            waitForElementToBeVisible(classTestSearchInput);
            return classTestSearchInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFailedMessageDisplayed(String message) {
        try {
            waitForElementToBeVisible(addTestButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@placeholder='Search test...']/following::div[contains(., '" + message + "')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
