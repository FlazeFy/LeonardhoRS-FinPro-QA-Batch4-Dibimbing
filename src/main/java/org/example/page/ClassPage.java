package org.example.page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.component.TableComponent;
import org.example.section.classmanagement.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import java.util.Map;

public class ClassPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    // Text
    @FindBy(xpath = "//p[normalize-space()='Manage Class']")
    private WebElement classManagementSectionTitle;

    private final ClassManagementSection classManagement;
    private final AnnouncementSection announcement;
    private final ContentSection content;
    private final MentorSection mentor;
    private final TestSection test;
    private final TableComponent table;

    public ClassPage(WebDriver driver) {
        super(driver);
        this.classManagement = new ClassManagementSection(driver);
        this.announcement = new AnnouncementSection(driver);
        this.content = new ContentSection(driver);
        this.mentor = new MentorSection(driver);
        this.test = new TestSection(driver);
        this.table = new TableComponent(driver);
    }

    public WebElement getClassAddMentorSectionTitle() {
        return this.mentor.classAddMentorSectionTitle;
    }

    public WebElement getClassMentorSectionTitle() {
        return this.mentor.classMentorSectionTitle;
    }

    public WebElement getClassTestSectionTitle() {
        return this.test.classTestSectionTitle;
    }

    // Class Management
    public boolean isClassManagementSectionTitleDisplayed() {
        try {
            waitForElementToBeVisible(classManagementSectionTitle);
            return classManagementSectionTitle.isDisplayed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isClassContentSectionTitleDisplayed() {
        return this.content.isSectionTitleDisplayed();
    }

    public boolean isClassUpdateContentSectionTitleDisplayed() {
        return this.content.isUpdateSectionTitleDisplayed();
    }

    // Class Item
    public List<Map<String, String>> getClassCardData() {
        return this.classManagement.getData();
    }

    public void openClassDetail(String title) {
        this.classManagement.openDetail(title);
    }

    public boolean isClassFailedMessageDisplayed(String message) {
        return this.classManagement.isFailedMessageDisplayed(message);
    }

    public boolean isClassContentFailedMessageDisplayed(String message) {
        return this.content.isFailedMessageDisplayed(message);
    }

    public boolean isClassListDisplayed() {
        return this.classManagement.isListDisplayed();
    }

    public boolean isContentListDisplayed() {
        return this.content.isListDisplayed();
    }

    public List<Map<String, String>> getContentCardData() {
        return this.content.getData();
    }

    // For Assertion
    public boolean isSearchClassDisplayed() {
        return this.classManagement.isSearchDisplayed();
    }

    public boolean isSearchClassContentDisplayed() {
        return this.content.isSearchDisplayed();
    }

    public boolean isFilterClassBatchDisplayed() {
        return this.classManagement.isFilterBatchDisplayed();
    }

    public boolean isDeleteAnnouncementModalDisplayed() {
        return this.announcement.isDeleteModalDisplayed();
    }

    // Action
    public void fillSearchClass(String classTitle) {
        this.classManagement.fillSearch(classTitle);
    }

    public void fillSearchClassContent(String classContentTitle) {
        this.content.fillSearch(classContentTitle);
    }

    public void fillCreateContent(
            String contentTitle, String contentDesc, String checkInKey, String checkOutKey, String contentPreTestUrl,
            String contentLinkMeeting, String liveClassDuration, String liveClassDate) {
        this.content.fillCreate(contentTitle, contentDesc, checkInKey, checkOutKey, contentPreTestUrl, contentLinkMeeting, liveClassDuration, liveClassDate);
    }

    public void fillEditContent(
            String contentTitle, String contentDesc, String checkInKey, String checkOutKey, String contentPreTestUrl,
            String contentLinkMeeting, String liveClassDuration, String liveClassDate) {
        this.content.fillEdit(contentTitle, contentDesc, checkInKey, checkOutKey,contentPreTestUrl, contentLinkMeeting, liveClassDuration, liveClassDate);
    }

    public void clickSubmitAnnouncement() {
        this.announcement.clickSubmit();
    }

    public void clickSubmitEditAnnouncement() {
        this.announcement.clickSubmitEdit();
    }

    public void openTabByTitle(String title) {
        try {
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@role='tablist']//button[@role='tab'][normalize-space()='" + title + "']")
            ));

            tab.click();

            // Wait until the clicked tab becomes active
            wait.until(ExpectedConditions.attributeToBe(tab, "aria-selected", "true"));
        } catch (TimeoutException e) {
            throw new NoSuchElementException("Tab not found or could not be opened: " + title, e);
        }
    }

    public boolean findAnnouncementByTitle(String title) {
        return this.announcement.findByTitle(title);
    }

    public void clickEditAnnouncementByTitle(String title) {
        this.announcement.clickEditByTitle(title);
    }

    public void clickDeleteAnnouncementByTitle(String title) {
        this.announcement.clickDeleteByTitle(title);
    }

    public void clickAddContentPageButton() {
        this.content.clickAddButton();
    }

    public void clickSubmitContent() {
        this.content.clickSubmit();
    }

    public void clickSubmitUpdateContent () {
        this.content.clickSubmitUpdate();
    }

    public void selectClassBatch(String batch) {
        this.classManagement.selectClassBatch(batch);
    }

    public void clickAContent(String contentTitle) {
        this.content.clickAnItem(contentTitle);
    }

    public void deleteAContent(String contentTitle) {
        this.content.deleteAnItem(contentTitle);
    }

    public void openEditContent(String contentTitle) {
        this.content.openEdit(contentTitle);
    }

    public void toggleHideContent(String contentTitle, String type) {
        this.content.toggleHide(contentTitle, type);
    }

    public boolean isContentToggleButtonDisplayed(String contentTitle, String type) {
        return this.content.isToggleButtonDisplayed(contentTitle, type);
    }

    public void setAttendanceEnabled(boolean enabled) {
        this.content.setAttendanceEnabled(enabled);
    }

    public void waitForPageLoading() {
        wait.until(driver ->
            driver.findElements(By.cssSelector("#nprogress")).isEmpty()
        );
    }

    // Announcement Section
    public List<Map<String, String>> getAnnouncementCardData() {
        return this.announcement.getData();
    }

    public boolean isAnnouncementListDisplayed() {
        return this.announcement.isListDisplayed();
    }

    public boolean isClassAnnouncementSectionTitleDisplayed() {
        return this.announcement.isSectionTitleDisplayed();
    }

    public void fillCreateAnnouncement(String announcementTitle, String announcementDesc) {
        this.announcement.fillCreate(announcementTitle, announcementDesc);
    }

    public void fillEditAnnouncement(String announcementTitle, String announcementDesc) {
        this.announcement.fillEdit(announcementTitle, announcementDesc);
    }

    public void clickSubmitDeleteAnnouncement() {
        this.announcement.clickSubmitDelete();
    }

    // Mentor Section
    public boolean isClassMentorSectionTitleDisplayed() {
        return this.mentor.isSectionTitleDisplayed();
    }

    public boolean isClassMentorFailedMessageDisplayed(String message) {
        return this.mentor.isFailedMessageDisplayed(message);
    }

    public boolean isClassAddMentorFailedMessageDisplayed(String message) {
        return this.mentor.isAddFailedMessageDisplayed(message);
    }

    public boolean isSearchClassMentorDisplayed() {
        return this.mentor.isSearchDisplayed();
    }

    public void fillSearchClassMentor(String classMentorName) {
        this.mentor.fillSearch(classMentorName);
    }

    public void openAddMentor() {
        this.mentor.openAdd();
    }

    public void assignAddMentor() {
        this.mentor.assignAdd();
    }

    public void confirmAddMentor() {
        this.mentor.confirmAdd();
    }

    public boolean isAddClassMentorPopupDisplayed() {
        return this.mentor.isAddPopupDisplayed();
    }

    public boolean isSearchAddClassMentorDisplayed() {
        return this.mentor.isSearchAddDisplayed();
    }

    public boolean isSearchAddClassMentorDisappear() {
        return this.mentor.isSearchAddDisappear();
    }

    public void fillSearchAddClassMentor(String mentorName) {
        this.mentor.fillSearchAdd(mentorName);
    }

    public String selectFirstMentorAndGetName() {
        return this.mentor.selectFirstAndGetName();
    }

    public boolean isSelectedMentorValid(String selectedMentor) {
        return this.mentor.isSelectedValid(selectedMentor);
    }

    // Test Section
    public boolean isClassTestSectionTitleDisplayed() {
        return this.test.isSectionTitleDisplayed();
    }

    public boolean isSearchClassTestDisplayed() {
        return this.test.isSearchDisplayed();
    }

    public void fillSearchClassTest(String testName) {
        this.test.fillSearchAdd(testName);
    }

    public void clickAddTestButton() {
        this.test.clickAddButton();
    }

    public void setTestContentType(String testContentType) {
        this.test.setTestContentType(testContentType);
    }

    public void fillCreateTest(
        String testTitle, String testType, String testDurationMethod, String duration, String mentorName, String startDate, String contentTestType
    ) {
        this.test.fillCreate(testTitle, testType, testDurationMethod, duration, mentorName, startDate, contentTestType);
    }

    public void clickSubmitTest() {
        this.test.clickSubmit();
    }

    public boolean isClassTestFailedMessageDisplayed(String message) {
        return this.test.isFailedMessageDisplayed(message);
    }

    // Table Component
    public boolean isTableDisplayed(WebElement sectionTitleElement, WebElement afterElement) {
        return this.table.isDisplayed(sectionTitleElement, afterElement);
    }

    public boolean isTableDataValid(WebElement sectionTitleElement, WebElement afterElement, String tagName) {
        return this.table.isDataValid(sectionTitleElement, afterElement, tagName);
    }

    public List<Map<String, String>> getTableData(WebElement sectionTitleElement, WebElement afterElement) {
        return this.table.getData(sectionTitleElement, afterElement);
    }
}
