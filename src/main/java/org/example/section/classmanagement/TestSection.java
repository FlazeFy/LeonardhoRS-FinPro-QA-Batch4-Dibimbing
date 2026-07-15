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

    // Text Element
    @FindBy(xpath = "//p[normalize-space()='List Test']")
    public WebElement classTestSectionTitle;

    // Button Element
    @FindBy(id = "create-class-test-button")
    private WebElement addTestButton;

    public TestSection(WebDriver driver) {
        super(driver);
    }

    // Fill Input Action
    public void fillSearchAdd(String mentorName) {
        classTestSearchInput.sendKeys(mentorName);
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
