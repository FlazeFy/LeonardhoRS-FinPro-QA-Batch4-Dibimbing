package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class AllPage extends BasePage {
    @FindBy(className = "chakra-toast")
    private WebElement responseToast;

    @FindBy(xpath = "//li[contains(@class,'chakra-toast')]//p")
    private List<WebElement> responseToastTexts;

    public AllPage(WebDriver driver) {
        super(driver);
    }

    public String getResponsePopUpText() {
        waitForElementToBeVisible(responseToast);
        wait.until(ExpectedConditions.visibilityOfAllElements(responseToastTexts));

        return responseToastTexts.stream().map(WebElement::getText).filter(text -> !text.isBlank()).collect(Collectors.joining(" "));
    }
}
