package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    // Field element
    @FindBy(id = "input-username-or-email")
    private WebElement usernameInput;

    @FindBy(id = "input-password")
    private WebElement passwordInput;

    // Button element
    @FindBy(id = "button-sign-in")
    private WebElement loginButton;

    // Text element
    @FindBy(xpath = "//input[@id='input-username-or-email']/following-sibling::p")
    private WebElement loginErrorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // Assertion
    public boolean isErrorMessageDisplayed() {
        try {
            waitForElementToBeVisible(loginErrorMessage);
            return loginErrorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Check last rendered text
    protected String waitForStableText(WebElement element, int timeoutSeconds) {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        String previous = "";
        String current = "";

        while (System.currentTimeMillis() < endTime) {
            current = element.getText();
            if (!current.isEmpty() && current.equals(previous)) {
                return current;
            }
            previous = current;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return current;
    }

    public String getErrorMessage() {
        try {
            waitForElementToBeVisible(loginErrorMessage);
            return waitForStableText(loginErrorMessage, 5);
        } catch (Exception e) {
            return "";
        }
    }

    // Action
    public void fillLoginCredentials(String username, String password) {
        waitForElementToBeVisible(usernameInput);
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
    }

    public void clickSignIn() {
        loginButton.click();
    }
}
