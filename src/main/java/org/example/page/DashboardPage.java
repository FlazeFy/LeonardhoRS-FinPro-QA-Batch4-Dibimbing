package org.example.page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class DashboardPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    // Button Element
    @FindBy(id = "menu-button-layout-desktop-menu-settings")
    private WebElement profileButton;

    @FindBy(id = "menu-list-layout-desktop-menu-settings-menuitem-:r9:")
    private WebElement signOutButton;

    @FindBy(id = "layout-desktop-menu-item-box-class")
    private WebElement classMenuButton;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    // Visibility Action
    private List<WebElement> waitForCompanyLogos() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[alt='Logo Company']")));

        return driver.findElements(By.cssSelector("img[alt='Logo Company']"));
    }

    // Make it dynamic, because on desktop there are two logos, while on mobile there is only one
    private WebElement getCompanyInfoElement(int infoIndex) {
        List<WebElement> logos = waitForCompanyLogos();
        if (logos.isEmpty()) throw new NoSuchElementException("Company logo not found.");

        int lastLogoIndex = logos.size();

        return driver.findElement(
                By.xpath("(//img[@alt='Logo Company'])[" + lastLogoIndex + "]/following::p[" + infoIndex + "]")
        );
    }

    // Retry with a fresh lookup each attempt so re-renders can't cause a stale element crash
    public boolean isCompanyLogoDisplayed() {
        try {
            wait.until(driver -> {
                try {
                    List<WebElement> logos = driver.findElements(By.cssSelector("img[alt='Logo Company']"));
                    if (logos.isEmpty()) return false;

                    WebElement freshLogo = logos.get(logos.size() - 1);

                    return freshLogo.isDisplayed();
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Company logo visibility check failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    // Get Data Action
    public String getCompanyName() {
        WebElement element = getCompanyInfoElement(1);
        waitForElementToBeVisible(element);

        return element.getText();
    }

    public String getCompanyAddress() {
        WebElement element = getCompanyInfoElement(2);
        waitForElementToBeVisible(element);

        return element.getText();
    }

    public String getCompanyPhone() {
        WebElement element = getCompanyInfoElement(3);
        waitForElementToBeVisible(element);

        return element.getText();
    }

    public String getCompanyEmail() {
        WebElement element = getCompanyInfoElement(4);
        waitForElementToBeVisible(element);

        return element.getText();
    }

    private String getValueByLabel(String labelText, String direction, String tag) {
        WebElement valueElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[text()='" + labelText + "']/" + direction + "-sibling::" + tag + "[1]"))
        );

        wait.until(driver -> !valueElement.getText().trim().isEmpty());

        return valueElement.getText().trim();
    }

    public String getCompanyVision() {
        return getValueByLabel("Company Vision", "following", "p");
    }

    public String getCompanyMission() {
        return getValueByLabel("Company Mission", "following", "p");
    }

    public String getWorkCulture() {
        return getValueByLabel("Work Culture", "following", "p");
    }

    public String getTotalEmployee() {
        return getValueByLabel("Total Employee", "preceding","p");
    }

    public String getTotalFastTrack() {
        return getValueByLabel("Total Fast Track Class", "preceding", "p");
    }

    public String getTotalFastTrackClass() {
        return getValueByLabel("Fast Track Class Active", "following", "p");
    }

    public String getFastTrackClassFinished() {
        return getValueByLabel("Fast Track Class Finished", "following", "p");
    }

    public String getNearestFastTrackClassMessage() {
        return getValueByLabel("Nearest Fast Track Class", "following", "div");
    }

    // Click Action
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

    public void clickClassMenuButton() {
        try {
            waitForElementToBeVisible(classMenuButton);
            classMenuButton.click();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
