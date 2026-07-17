package org.example.section.classmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.page.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClassManagementSection extends BasePage {
    private static final Logger logger = LogManager.getLogger(ClassManagementSection.class);

    // Input Element
    @FindBy(xpath = "//input[@placeholder='Search class...']")
    private WebElement searchClassInput;

    @FindBy(id = "update-class-step2-avg-test-score-percentage-input")
    private WebElement assessmentComponentAverageTestInput;

    @FindBy(id = "create-class-page-title-input")
    private WebElement classTitleInput;

    @FindBy(id = "create-class-page-start-date-input")
    private WebElement classStartDateInput;

    @FindBy(id = "create-class-page-end-date-input")
    private WebElement classEndDateInput;

    @FindBy(id = "create-class-page-enrollment-key-input")
    private WebElement classEnrollmentKeyInput;

    @FindBy(id = "create-class-page-class-syllabus-input")
    private WebElement classSyllabusUrlInput;

    // Checkbox Element
    @FindBy(id = "update-class-step2-avg-test-score-percentage-checkbox")
    private WebElement assessmentComponentAverageTestCheckbox;

    // Select Element
    @FindBy(xpath = "//button[.//p[normalize-space()='Filter by Angkatan']]")
    private WebElement filterClassBatchSelect;

    // Button Element
    @FindBy(xpath = "//button[normalize-space()='Add New Class']")
    private WebElement addNewClassButton;

    @FindBy(id = "create-class-page-next-button")
    private WebElement submitClassForm;

    @FindBy(id = "update-class-step2-next-button")
    private WebElement submitClassAssessmentFormButton;

    @FindBy(id = "update-class-step3-next-button")
    private WebElement submitReviewCreateClassFormButton;

    // Text Element
    @FindBy(xpath = "//p[normalize-space()='Assessment Components']")
    private WebElement assessmentComponentSectionTitle;

    @FindBy(xpath = "//p[normalize-space()='Upload Class Syllabus']")
    private WebElement uploadClassSyllabusSectionTitle;

    @FindBy(xpath = "//p[normalize-space()='Add Class']")
    private WebElement createClassPageTitle;

    public ClassManagementSection(WebDriver driver) {
        super(driver);
    }

    // Click Action
    public void openCreatePage() {
        waitForElementToBeVisible(addNewClassButton);
        addNewClassButton.click();
    }

    public void clickSubmitClassForm() {
        waitForElementToBeVisible(submitClassForm);
        submitClassForm.click();
    }

    public void clickSubmitClassReviewForm() {
        waitForElementToBeVisible(submitReviewCreateClassFormButton);
        submitReviewCreateClassFormButton.click();
    }

    public void clickSubmitClassAssessmentForm() {
        waitForElementToBeVisible(submitClassAssessmentFormButton);
        submitClassAssessmentFormButton.click();
    }

    // Select Action
    public void selectClassBatch(String batch) {
        try {
            filterClassBatchSelect.click();
            WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@role='menu']//button[@role='menuitem'][normalize-space(.)='" + batch + "']")
            ));
            menuItem.click();

            // Confirm the dropdown menu is hidden (render finish)
            wait.until(driver -> {
                WebElement menu = driver.findElement(By.xpath(
                        "//button[.//p[normalize-space()='Filter by Angkatan']]/following::div[@role='menu'][1]"
                ));

                String visibility = menu.getCssValue("visibility");
                return "hidden".equalsIgnoreCase(visibility);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectDropdownOption(String fieldTitle, String value) {
        // Locate the field container by its title
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='" + fieldTitle + "']/ancestor::div[@role='group'][1]")
        ));

        // Click the dropdown button
        WebElement button = field.findElement(By.xpath(".//button[@aria-haspopup='menu']"));
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();

        // Select the option
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='menu']//button[normalize-space()='" + value + "']")
        ));
        option.click();
    }

    // Visibility Action
    public boolean isSearchDisplayed() {
        try {
            waitForElementToBeVisible(searchClassInput);
            return searchClassInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFailedMessageDisplayed(String message) {
        try {
            waitForElementToBeVisible(addNewClassButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[normalize-space()='Add New Class']/following::div[contains(., '" + message + "')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isFilterBatchDisplayed() {
        try {
            waitForElementToBeVisible(filterClassBatchSelect);
            return filterClassBatchSelect.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCreatePageTitleDisplayed() {
        try {
            waitForElementToBeVisible(createClassPageTitle);
            return createClassPageTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Select Radio Group
    private void selectClassSyllabusType(String syllabusType) {
        waitForElementToBeVisible(uploadClassSyllabusSectionTitle);

        WebElement radioOption = uploadClassSyllabusSectionTitle.findElement(
                By.xpath("./ancestor::div[@role='group'][1]//label[.//p[normalize-space()='" + syllabusType + "']]")
        );

        wait.until(ExpectedConditions.elementToBeClickable(radioOption)).click();
    }

    // Select Checkbox Group
    private void selectAssessmentComponent(String componentTitle) {
        waitForElementToBeVisible(assessmentComponentSectionTitle);

        WebElement checkbox = assessmentComponentSectionTitle.findElement(
                By.xpath("./ancestor::div[1]/following-sibling::div//label[.//p[normalize-space()='" + componentTitle + "']]")
        );

        wait.until(ExpectedConditions.elementToBeClickable(checkbox)).click();
    }

    public void selectDate(WebElement input, String dateTime) {
        wait.until(ExpectedConditions.elementToBeClickable(input)).click();

        LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.ENGLISH));

        // Wait for calendar
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("month-select")));

        // Select month, year, and day
        new Select(driver.findElement(By.id("month-select"))).selectByIndex(localDate.getMonthValue() - 1);
        new Select(driver.findElement(By.id("year-select"))).selectByVisibleText(String.valueOf(localDate.getYear()));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("date-this-month-" + localDate.getDayOfMonth()))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("apply-button"))).click();

        // Wait until calendar closes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("apply-button")));
    }

    // Fill Input Action
    public void fillSearch(String classTitle) {
        searchClassInput.sendKeys(classTitle);
    }

    public void fillCreateClass(
            String classTitle, String classDesc, String batch, String major, String startDate, String endDate,
            String enrollmentKey, String classSyllabusType, String classSyllabusUrl) {
        // Class Title
        waitForElementToBeVisible(classTitleInput);
        classTitleInput.sendKeys(classTitle);
        // Class Desc
        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='Description']/following::div[@role='textbox'][1]")));
        editor.sendKeys(classDesc);
        // Class Batch / Angkatan
        selectDropdownOption("Angkatan (Optional)", batch);
        // Class Major / Program Stuid
        // selectDropdownOption("Program Studi (Optional)", major);
        // Class Start Date
        waitForElementToBeVisible(classStartDateInput);
        selectDate(classStartDateInput, startDate);
        // Class End Date
        waitForElementToBeVisible(classEndDateInput);
        selectDate(classEndDateInput, endDate);
        // Class Enrollment Key
        waitForElementToBeVisible(classEnrollmentKeyInput);
        classEnrollmentKeyInput.sendKeys(enrollmentKey);
        // Class Syllabus Type
        selectClassSyllabusType(classSyllabusType);
        // Class Syllabus Url
        waitForElementToBeVisible(classSyllabusUrlInput);
        classSyllabusUrlInput.sendKeys(classSyllabusUrl);
    }

    public void fillCreateClassAssessment(String assessmentComponentSelected, String assessmentComponentPercentage) {
        // Assessment Component Selected
        selectAssessmentComponent(assessmentComponentSelected);
        // Assessment Component Percentage
        waitForElementToBeVisible(assessmentComponentAverageTestInput);
        assessmentComponentAverageTestInput.sendKeys(assessmentComponentPercentage);
    }

    // Navigate Action
    public void openDetail(String title) {
        List<WebElement> cards = getElement();

        for (WebElement card : cards) {
            String cardTitle = card.findElement(By.xpath(".//p[1]")).getText().trim().toLowerCase();

            if (cardTitle.equals(title.trim().toLowerCase())) {
                WebElement editButton = card.findElement(By.xpath(".//button[normalize-space()='Edit & Manage Content']"));
                wait.until(ExpectedConditions.elementToBeClickable(editButton));
                editButton.click();
                return;
            }
        }

        throw new NoSuchElementException("No class card found with title: " + title);
    }

    // Validate List Item
    private List<WebElement> getElement() {
        waitForElementToBeVisible(addNewClassButton);

        // wait for the skeleton loading state to clear before reading real content
        wait.until(driver -> {
            List<WebElement> images = driver.findElements(By.cssSelector("img[alt='bootcamp image']"));
            return !images.isEmpty();
        });

        return driver.findElements(
                By.xpath("//button[normalize-space()='Add New Class']/following::img[@alt='bootcamp image']/ancestor::div[3]")
        );
    }

    public List<Map<String, String>> getData() {
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement card : getElement()) {
            Map<String, String> data = new HashMap<>();

            data.put("title", card.findElement(By.xpath(".//p[1]")).getText().trim());
            data.put("major", card.findElement(By.xpath(".//p[contains(.,'Program Studi:')]"))
                    .getText().replace("Program Studi:", "").trim());
            data.put("batch", card.findElement(By.xpath(".//p[contains(.,'Angkatan:')]"))
                    .getText().replace("Angkatan:", "").trim());
            data.put("employee", card.findElement(By.xpath(".//p[contains(.,'Employee join this class')]"))
                    .getText().replace("Employee join this class", "").trim());
            data.put("startDate", card.findElement(By.xpath(".//p[contains(.,'Started at')]"))
                    .getText().replace("Started at", "").trim());

            result.add(data);
        }

        return result;
    }

    public boolean isListDisplayed() {
        try {
            List<WebElement> data = getElement();
            if (data.isEmpty()) return false;

            int idx = 0;
            for (WebElement dt : data) {
                // validate class image
                boolean hasImage = !dt.findElements(By.cssSelector("img[alt='bootcamp image']")).isEmpty();

                // validate class title
                boolean hasTitle = !dt.findElements(By.xpath(".//p[1]")).isEmpty();

                // validate edit and manage content button
                boolean hasEditButton = !dt.findElements(By.xpath(".//button[contains(text(),'Edit & Manage Content')]")).isEmpty();

                // validate delete class button
                boolean hasDeleteButton = !dt.findElements(By.xpath(".//button[contains(text(),'Delete Class')]")).isEmpty();

                // validate program studi info
                boolean hasMajor = !dt.findElements(By.xpath(".//p[contains(text(),'Program Studi:')]")).isEmpty();

                // validate angkatan info
                boolean hasBatch = !dt.findElements(By.xpath(".//p[contains(text(),'Angkatan:')]")).isEmpty();

                // validate employee join info
                // boolean hasEmployeeJoin = !card.findElements(By.xpath(".//p[contains(text(),'Employee join this class')]")).isEmpty();
                boolean hasEmployeeJoin = !dt.findElements(By.xpath(".//p[contains(.,'Employee join this class')]")).isEmpty();

                // validate class start date info
                boolean hasStartDate = !dt.findElements(By.xpath(".//p[contains(text(),'Started at')]")).isEmpty();

                if (!(hasImage && hasTitle && hasEditButton && hasDeleteButton && hasMajor && hasBatch && hasEmployeeJoin && hasStartDate)) {
                    System.out.println("Class invalid at - "+idx);
                    return false;
                }

                idx++;
            }

            return true;
        } catch (Exception e) {
            logger.error("Class list validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}
