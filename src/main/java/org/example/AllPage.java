package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AllPage extends BasePage {
    @FindBy(className = "chakra-toast")
    private WebElement responseToast;

    public AllPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//li[contains(@class,'chakra-toast')]//p")
    private WebElement responseToastText;

    public String getResponsePopUpText() {
        waitForElementToBeVisible(responseToast);
        waitForElementToBeVisible(responseToastText);

        return responseToastText.getText().trim();
    }
}
