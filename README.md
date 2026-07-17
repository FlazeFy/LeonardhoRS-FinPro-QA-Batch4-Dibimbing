# 🎓 LMS B2B DIBIMBING - QA & AUTOMATION TEST

> **LMS B2B Dibimbing QA** is the **quality assurance and test automation suite** for the LMS B2B Dibimbing platform — an internal, admin-only **web application** used by Dibimbing to manage bootcamp/course programs. This repository covers the **API Integration Testing** (Java REST Assured) and **UI End-to-End Testing** (Java Selenium), also with implementing **compatibility testing** across browsers, and **regression testing** through GitHub Actions CI/CD. The suite validates core capabilities such as company dashboard, class management (content, mentors, test, submission, and announcement), and announcement management, ensuring the platform is stable, secure, and consistent across Google Chrome and Mozilla Firefox.

## 📋 Basic Information

If you want to see the project detail documentation, you can read the QA documentation below.

1. **Test Strategy & Plan**
   https://docs.google.com/document/d/1c4tvkDNgWVm6nBsyxz45QrE_KsE-42GhOuemr1caSFs/edit?usp=sharing — Test scope, test strategy, test data, entry & exit criteria (this document)

2. **Test Case & Test Result Logging**
   https://docs.google.com/spreadsheets/d/1KjT3f-P2e1mhddSH_ZcvmIitB2Eax6VoZsLhQN7y-bo/edit?usp=sharing — Test Case - LMS B2B Dibimbing - LeonardhoRS (Google Spreadsheet)

3. **Bug / Defect Logging**
   https://docs.google.com/spreadsheets/d/15asQTbJZ-2HD-x1XVjg8Y5sgrqCcXBEvdlqZbgSWufk/edit?usp=sharing — Bug Report - LMS B2B Dibimbing - LeonardhoRS (Defect Management)

4. **Test Schedule (Jira Sprint Board)**
   https://flazenedu-1783068267561.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog

### 🌐 Test Environment URL

- Web (Staging) : https://lms-b2b.do.dibimbing.id/dibimbingqa/login
- GraphQL API Docs : https://lmsb2b.do.dibimbing.id/graphql

---

## 🎯 Product Overview

- **Authentication**
  Admin/employee users can log in to the app using basic auth and sign out securely.

- **Dashboard**
  Users can view the company profile and a fast-track summary of ongoing programs.

- **Class Management**
  Users can view all classes, add new classes, and manage class activities — including announcements, tests, content, mentor and employee assignments, report exports, and editing class information.

- **Manage Announcement**
  Users can view all announcements, broadcast new ones, and edit or delete existing announcements.

- **Cross-Browser Compatibility**
  The application is validated across Google Chrome, Mozilla Firefox, and Microsoft Edge to ensure consistent behavior on desktop devices.

## 🚀 Target Users

1. **QA Engineer**
   Plans, scripts, executes, and maintains manual and automated test coverage across all modules, and tracks defects through resolution.

2. **Developers**
   Rely on the automation suite (Selenium + REST Assured) and CI/CD pipeline results to validate changes before and after merging code.

3. **System Analyst**
   Reviews and approves test cases, requirements, and manual testing sign-off prior to release.

4. **Project Manager / Stakeholders**
   Use the Test Closure Report, QA Metrics, and Go/No-Go recommendation to make release decisions.

## 🧠 Problem to Solve

1. Manual-only testing of a growing admin platform is **slow and error-prone**, especially for regression coverage as new features are added to Class Management.
2. Without automation, **cross-browser verification** (Chrome, Firefox, Edge) requires repeating the same manual steps three times, wasting the limited testing window.
3. Untracked defects and inconsistent severity/priority classification can lead to **critical issues shipping unnoticed** (e.g., data leaks, broken class scheduling).
4. Without CI/CD integration, regressions introduced by new code changes are **caught late**, after manual testers stumble onto them instead of automatically on every merge.
5. Sensitive data handling (GraphQL error responses, file uploads, meeting links) needs **structured security verification**, not just functional checks.

## 💡 Solution

1. Adopt a **layered testing pyramid strategy**: automated API Integration Testing (REST Assured), automated UI E2E Testing (Selenium), manual System Testing, and manual+automated Compatibility/Regression Testing.
2. Run the **same Selenium suite across a CI build matrix** (Chrome + Firefox in parallel jobs) instead of manually repeating test execution per browser, reserving manual browser checks only for P1 layout/rendering spot-checks.
3. Integrate **GitHub Actions CI/CD** so API and UI regression suites run automatically on every push/merge to `main`, with `workflow_run` chaining to sequence dependent modules (e.g., Auth before Class Management).
4. Maintain a structured **Bug/Defect and Security Vulnerability log** (Google Spreadsheet) with defined severity (S1–S4) and priority (P1–P4) classification and SLA-based resolution targets.
5. Apply the **Page Object Model** with tab-based Section classes for maintainability, so locators and actions stay organized as the LMS UI grows more complex.

## 🔗 Features

- 🧪 API Integration Testing (Java REST Assured)
- 🖥️ UI End-to-End Testing (Java Selenium WebDriver)
- 📝 Manual System Testing
- 🌐 Compatibility Testing (Chrome, Firefox, Edge — CI build matrix + manual P1 spot-check)
- 🔁 Regression Testing (Manual + GitHub Actions CI/CD)
- 🐞 Defect & Security Vulnerability Tracking
- 📊 Automation Test Reporting (ExtentReports)
- 📧 Automated Test Report Delivery via Email

---

## 🛠️ Tech Stack

### Automation Test

- Java 17
- Selenium WebDriver 4.x — UI/E2E Testing
- REST Assured — API Integration Testing
- TestNG — Test runner, suite orchestration, parallel execution
- Gradle (Kotlin DSL) — Build tool

### Reporting & Logging

- ExtentReports — HTML test execution reports
- Apache POI / POI-OOXML — Excel/spreadsheet-based test data handling
- Log4j2 (Core + API) — Logging

### Test Data & Utilities

- Apache Commons IO
- AssertJ — Fluent assertions
- WebDriverManager (Bonigarcia) — Local driver management

### Infrastructure & Deployment

- Git & GitHub — Code repository
- GitHub Actions — CI/CD (test execution matrix, secrets-based config, email report delivery)
- Jira — Sprint planning & test schedule tracking
- Google Spreadsheet — Test Case, Bug Report, Security Vulnerability logging

### Application Under Test

- React & Next.js (Frontend, Chakra UI)
- GraphQL (API layer)

---

## 🏗️ Architecture

### Structure

The project follows the **Page Object Model (POM)**, with large pages further broken down into **tab-based Section classes** (e.g., Class Management's Content, Announcement, and Mentor tabs), keeping `ClassPage` as a lightweight facade over its sections.

### 📁 Project Structure

| Directory/File | Purpose |
|----------------|--------|
| `src/main/java/org.example/page/` | Page Object classes representing full pages (e.g., `LoginPage`, `DashboardPage`, `ClassPage`). |
| `src/main/java/org.example/section/` | Tab-based Section classes (e.g., `AnnouncementSection`, `ContentSection`, `MentorSection`) holding locators/actions scoped to one tab. |
| `src/main/java/org.example/component/` | Reusable UI components shared across pages/sections (e.g., table handling). |
| `src/main/java/org.example/selector/` | Shared, reusable locators not owned by a single page (e.g., Chakra modal patterns, generic button-by-text builders). |
| `src/test/java/core/` | Test framework core: `BaseTest`, `BaseApiTest`, `DriverManager`, `ConfigReader`, `DataGenerator`, `RetryAnalyzer`, `RetryListener`, `TestDataReader`, `TestListener`, `TestUtil`. |
| `src/test/java/test/e2e/` | UI/E2E Selenium test classes, grouped by module (`auth/`, `classmanagement/`, `dashboard/`). |
| `src/test/java/test/integration/` | API Integration REST Assured test classes, grouped by module (`auth/`, `classmanagement/`, `dashboard/`). |
| `src/test/resources/config/` | Environment property files (e.g., `staging.properties`) — gitignored, generated at runtime in CI from GitHub Secrets. |
| `src/test/resources/suites/` | TestNG suite XML files (e.g., `smoke.xml`, per-module CI suites) defining which test classes run per execution. |
| `src/test/resources/test-data/` | Test data files (`data.csv`) used across test cases. |
| `src/test/resources/log4j2.xml` | Logging configuration. |
| `.github/workflows/` | GitHub Actions CI/CD workflow definitions (per-module test triggers, cross-browser matrix, report email delivery). |
| `reports/` | Generated ExtentReports HTML output per test run. |
| `build.gradle.kts` | Gradle build config — supports `-Psuite=`, `-Penv=`, `-Pbrowser=` flags for flexible execution. |

---

### 🧾 Environment Variables

To set up the environment, create a `staging.properties` file inside `src/test/resources/config/`. In CI, this file is generated at runtime from GitHub Secrets rather than committed to the repository.

| Variable Name | Description | Example |
|----------------|--------------|---------|
| `baseUrl` | Base URL of the web application under test. | `https://lms-b2b.do.dibimbing.id/dibimbingqa/login` |
| `baseApiUrl` | Base URL of the GraphQL API under test. | `https://lmsb2b.do.dibimbing.id/graphql` |
| `usernameGraphQl` | Username used for GraphQL API authentication. | `qa_user` |
| `passwordGraphQl` | Password used for GraphQL API authentication. | `********` |
| `companyId` | Company ID used as test context for B2B data. | `1024` |
| `validEmailAuth` | Valid registered email for login tests. | `arwendymelyn@dibimbing.id` |
| `invalidUnregisteredEmailAuth` | Email not registered in the system, for negative login tests. | `unregistered@dibimbing.id` |
| `invalidEmailAuth` | Malformed/invalid email format, for negative login tests. | `invalid-email` |
| `validPasswordAuth` | Valid password paired with `validEmailAuth`. | `********` |
| `invalidPasswordAuth` | Incorrect password, for negative login tests. | `wrongpass` |

---

## 🗓️ Testing Process

Testing follows a **staggered parallel model** across a 5-day cycle: manual testing and automation scripting run concurrently, with automation lagging manual execution by one day so scripts are written against triaged, confirmed expected results rather than unverified behavior. P1/P2 test cases (and their automation) are executed first; P3/P4 test cases follow later.

### Technical Challenges

- **Priority mismatch between manual and automation** — the priority defined in a manual test case does not always align with the priority attribute in the automation test case, requiring careful mapping during scripting.
- **Chakra UI dynamic class names** — auto-generated CSS classes (e.g., `css-xxxxx`) are unstable and build-dependent, so locators rely on `id`, `data-testid`, `aria` attributes, and stable structural XPath instead.
- **Cross-browser execution parity** — ensuring the same Selenium suite behaves consistently across Chrome and Firefox in CI (headless configuration, driver setup, and binary paths differ per browser).
- **GraphQL error handling** — some endpoints previously leaked raw SQL errors on invalid input, requiring dedicated security-focused test coverage in addition to functional assertions.
- **Flaky UI states** — skeleton loading states and async content rendering require explicit wait strategies; `RetryAnalyzer`/`RetryListener` are used to reduce flaky-test noise in CI.

---

## 🚀 Setup & Installation

### Prerequisites

#### 🔧 General
- Git installed
- A GitHub account
- Basic knowledge of Java, Software Testing, GraphQL, and the LMS B2B business process
- IntelliJ IDEA (or any Java-compatible IDE)
- Google Account (for Test Case/Bug Report spreadsheets)
- Jira account access

#### 🧠 Automation Test
- Java 17 (minimum)
- Gradle (Kotlin DSL — wrapper included via `gradlew`)
- Google Chrome and/or Mozilla Firefox installed locally
- Git (for cloning the repository)

### Installation Steps

**Local Init**
1. Clone this repository: `git clone https://github.com/FlazeFy/LeonardhoRS-FinPro-QA-Batch4-Dibimbing.git`
2. Create `staging.properties` under `src/test/resources/config/` with the required keys listed in [Environment Variables](#-environment-variables).
3. Grant execute permission to the Gradle wrapper (macOS/Linux): `chmod +x gradlew`
4. Verify the project builds: `./gradlew build`

---

## 📝 Notes & Limitations

### ⚠️ Precautions When Running the Tests
- Ensure the **staging environment is running and accessible** via the configured `baseUrl`/`baseApiUrl` before executing any test.
- Do not commit `staging.properties` or expose credentials in the repository — it is gitignored by design; CI generates it at runtime from GitHub Secrets.
- Avoid running automated regression suites against **production environments**.
- Ensure test account data (`validEmailAuth`, `validPasswordAuth`, etc.) is valid and not expired before running Auth-dependent test suites.
- Ensure the staging database/test environment is in a **consistent state** before running integration or E2E tests to reduce flaky results.
- Test cases must meet the exit criteria (≥90% pass rate, no open Critical/High defects) before being considered release-ready.

### 🧱 Technical Limitations
- Manual and System Testing are executed on **Google Chrome only** (primary browser) to keep manual execution efficient; Firefox and Edge coverage is achieved through automated CI execution instead.
- Compatibility testing on Firefox and Microsoft Edge is a **lightweight manual visual check scoped to P1 features only**, not a full re-execution of all test cases.
- Chakra UI's auto-generated class names cannot be relied upon as stable selectors.

### 🐞 Known Issues
- Unrestricted ZIP/file type upload in content management (S2/P1).
- Invalid URLs accepted in meeting link fields (VULN-CLMG-002/003).
- Edit Content allows past dates while Create correctly rejects them.
- Intermittent 500 error on Announcement detail (S2/P1).
- GraphQL endpoint previously leaked raw SQL errors on invalid non-UUID IDs (escalated to S1, VULN-ANCM-001).
- Whitespace-only input accepted in several validation fields.
- Zero-value duration accepted in class content duration field.

---

## 🧪 How To Run The Test

### API Integration Test and UI (E2E) Testing
Tools: Java REST Assured, Java Selenium, TestNG, Gradle

- Run the default suite (`smoke.xml`) against staging on Chrome:
  `./gradlew test`

- Run a specific suite file:
  `./gradlew test -Psuite=class-management-ci.xml`

- Run against a specific environment:
  `./gradlew test -Psuite=auth-api-ci.xml -Penv=staging`

- Run UI tests against a specific browser (Chrome or Firefox):
  `./gradlew test -Psuite=class-management-ci.xml -Penv=staging -Pbrowser=firefox`

### CI/CD (GitHub Actions)
Regression and per-module test suites run automatically via GitHub Actions on push to `main`, including a **cross-browser build matrix** (Chrome + Firefox running in parallel), with `staging.properties` generated at runtime from GitHub Secrets and test reports emailed automatically after each run.

_Made with ❤️ by Leonardho R. Sitanggang_