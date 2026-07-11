package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForUrlToContain(String partialUrl) {
        wait.until(ExpectedConditions.urlContains(partialUrl));
    }

    public void clearInput(WebElement element) {
        Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
        element.click();
        element.sendKeys(Keys.chord(modifier, "a"));
        element.sendKeys(Keys.DELETE);
    }

    public void clearRichText(WebElement element) {
        Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
        element.click();
        element.sendKeys(Keys.chord(modifier, "a"));
        element.sendKeys(Keys.DELETE);
    }

    protected void clearAndFill(WebElement element, String value) {
        waitForElementToBeVisible(element);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeInputValueSetter.call(arguments[0], '');" +
                        "var event = new Event('input', { bubbles: true });" +
                        "arguments[0].dispatchEvent(event);",
                element
        );

        element.sendKeys(value);
    }
}