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

public class AnnouncementSection extends BasePage {
    private static final Logger logger = LogManager.getLogger(AnnouncementSection.class);

    // Input Element - Announcement
    @FindBy(id = "create-announcement-title-input")
    private WebElement announcementTitleInput;

    @FindBy(id = "edit-announcement-title-input")
    private WebElement announcementTitleEditInput;

    @FindBy(id = "delete-announcement-delete-button")
    private WebElement announcementDeleteButton;

    @FindBy(xpath = "//p[normalize-space()='Announcement']")
    private WebElement classAnnouncementSectionTitle;

    // Richtext Editor Element
    @FindBy(id = "create-announcement-description-input")
    private WebElement announcementDescInput;

    @FindBy(id = "create-announcement-button")
    private WebElement submitAnnouncementButton;

    @FindBy(id = "edit-announcement-button")
    private WebElement submitEditAnnouncementButton;

    @FindBy(xpath = "//section[contains(@class,'chakra-modal__content') and @role='dialog']//header//p[normalize-space()='Delete Announcement']")
    private WebElement deleteAnnouncementModalTitle;

    public AnnouncementSection(WebDriver driver) {
        super(driver);
    }

    public void fillCreate(String announcementTitle, String announcementDesc) {
        waitForElementToBeVisible(announcementTitleInput);
        announcementTitleInput.sendKeys(announcementTitle);
        waitForElementToBeVisible(announcementDescInput);
        announcementDescInput.sendKeys(announcementDesc);
    }

    public void fillEdit(String announcementTitle, String announcementDesc) {
        waitForElementToBeVisible(announcementTitleEditInput);
        clearInput(announcementTitleEditInput);
        announcementTitleEditInput.sendKeys(announcementTitle);

        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-announcement-description-input")));
        clearRichText(editor);
        editor.sendKeys(announcementDesc);
    }

    public void clickSubmitDelete() {
        announcementDeleteButton.click();
    }

    public boolean isSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classAnnouncementSectionTitle);
            return classAnnouncementSectionTitle.isDisplayed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<WebElement> getListElement() {
        waitForElementToBeVisible(classAnnouncementSectionTitle);

        wait.until(driver ->
                !driver.findElements(By.xpath("//li[contains(@class,'chakra-stack')][.//h2]")).isEmpty()
        );

        return driver.findElements(By.xpath("//li[contains(@class,'chakra-stack')][.//h2]"));
    }

    public boolean isListDisplayed() {
        try {
            List<WebElement> data = getListElement();
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

    public List<Map<String, String>> getData() {
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement dt : getListElement()) {
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

    private void clickAnnouncementButton(String title, String type) {
        List<WebElement> cards = getListElement();

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

    public void clickEditByTitle(String title) {
        clickAnnouncementButton(title, "Edit");
    }

    public void clickDeleteByTitle(String title) {
        clickAnnouncementButton(title, "Delete");
    }

    public void clickSubmit() {
        submitAnnouncementButton.click();
    }

    public void clickSubmitEdit() {
        submitEditAnnouncementButton.click();
    }

    public boolean findByTitle(String title) {
        List<WebElement> cards = getListElement();

        for (WebElement card : cards) {
            String announcementTitle = card.findElement(By.xpath(".//h2")).getText().trim();
            if (announcementTitle.equalsIgnoreCase(title.trim())) return true;
        }

        return false;
    }

    public boolean isDeleteModalDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(deleteAnnouncementModalTitle));
            return deleteAnnouncementModalTitle.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
}
