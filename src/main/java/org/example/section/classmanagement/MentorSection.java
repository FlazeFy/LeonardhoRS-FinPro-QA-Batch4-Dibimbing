package org.example.section.classmanagement;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.component.TableComponent;
import org.example.page.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class MentorSection extends BasePage {
    private static final Logger logger = LogManager.getLogger(MentorSection.class);

    // Input Element - Mentor
    @FindBy(xpath = "//header[normalize-space()='Add Mentor']//following::input[@placeholder='Search name...']")
    private WebElement contentAddMentorSearchInput;

    @FindBy(xpath = "//input[@placeholder='Search name...']")
    private WebElement searchClassMentorInput;

    @FindBy(id = "button-add-mentor-button")
    private WebElement addMentorButton;

    @FindBy(id = "button-add-mentor-add-mentor-button")
    private WebElement assignMentorButton;

    @FindBy(id = "button-add-mentor-confirm-add-mentor-button")
    private WebElement confirmAddMentorButton;

    @Getter
    @FindBy(xpath = "//header[normalize-space()='Add Mentor']")
    public WebElement classAddMentorSectionTitle;

    @FindBy(xpath = "//p[normalize-space()='The following is the list of employees who have been selected to become mentors :']")
    private WebElement confirmationAddMentorText;

    @FindBy(xpath = "//p[normalize-space()='The following is the list of employees who have been selected to become mentors :']/following-sibling::ul")
    private WebElement listSelectedAddMentor;

    @FindBy(xpath = "//p[normalize-space()='Mentor List']")
    private WebElement classMentorSectionTitle;

    private final TableComponent table;

    public MentorSection(WebDriver driver) {
        super(driver);
        this.table = new TableComponent(driver);
    }

    public boolean isSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classMentorSectionTitle);
            return classMentorSectionTitle.isDisplayed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isFailedMessageDisplayed(String message) {
        try {
            waitForElementToBeVisible(addMentorButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[normalize-space()='Add Mentor']/following::div[contains(., '" + message + "')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isAddFailedMessageDisplayed(String message) {
        try {
            waitForElementToBeVisible(addMentorButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@placeholder='Search name...']/following::div[contains(., '" + message + "')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isSearchDisplayed() {
        try {
            waitForElementToBeVisible(searchClassMentorInput);
            return searchClassMentorInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void fillSearch(String classMentorName) {
        waitForElementToBeVisible(searchClassMentorInput);
        searchClassMentorInput.sendKeys(classMentorName);
    }

    public void openAdd() {
        waitForElementToBeVisible(addMentorButton);
        addMentorButton.click();
    }

    public void assignAdd() {
        waitForElementToBeVisible(assignMentorButton);
        assignMentorButton.click();
    }

    public void confirmAdd() {
        waitForElementToBeVisible(confirmAddMentorButton);
        confirmAddMentorButton.click();
    }

    public boolean isAddPopupDisplayed() {
        try {
            waitForElementToBeVisible(classAddMentorSectionTitle);
            return classAddMentorSectionTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSearchAddDisplayed() {
        try {
            waitForElementToBeVisible(contentAddMentorSearchInput);
            return contentAddMentorSearchInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSearchAddDisappear() {
        try {
            waitForElementToDisappear(classAddMentorSectionTitle);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void fillSearchAdd(String mentorName) {
        contentAddMentorSearchInput.sendKeys(mentorName);
    }

    public String selectFirstAndGetName() {
        try {
            WebElement table = this.table.getElement(classAddMentorSectionTitle);

            wait.until(driver -> {
                WebElement firstRow = table.findElement(By.xpath(".//tbody/tr[1]"));
                List<WebElement> cells = firstRow.findElements(By.tagName("td"));

                return cells.size() > 1 && !cells.get(1).getText().trim().isEmpty();
            });

            // First row
            WebElement firstRow = table.findElement(By.xpath(".//tbody/tr[1]"));
            List<WebElement> cells = firstRow.findElements(By.tagName("td"));

            // Get name
            String name = cells.get(1).getText().trim();

            // Click the SVG in the last column
            WebElement svg = wait.until(
                    ExpectedConditions.elementToBeClickable(firstRow.findElement(By.xpath("./td[last()]//*[name()='svg']")))
            );
            svg.click();

            return name;
        } catch (Exception e) {
            logger.error("Failed to select first row: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    public boolean isSelectedValid(String selectedMentor) {
        waitForElementToBeVisible(confirmationAddMentorText);
        waitForElementToBeVisible(listSelectedAddMentor);

        List<String> mentorNames = listSelectedAddMentor.findElements(By.tagName("li"))
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());

        return mentorNames.contains(selectedMentor);
    }
}
