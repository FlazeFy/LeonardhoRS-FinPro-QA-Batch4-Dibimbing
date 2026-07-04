package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends BasePage {
    // Button Element
    @FindBy(id = "menu-button-layout-desktop-menu-settings")
    private WebElement profileButton;

    @FindBy(id = "menu-list-layout-desktop-menu-settings-menuitem-:r9:")
    private WebElement signOutButton;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    // Action
    public void clickProfileButton() {
        try {
            waitForElementToBeVisible(profileButton);
            profileButton.click();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clickSignOutButton() {
        try {
            waitForElementToBeVisible(signOutButton);
            signOutButton.click();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
