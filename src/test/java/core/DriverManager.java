package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver(String browser) {
        WebDriver webDriver = null;
        switch (browser.toLowerCase()) {
            case "chrome" -> {
                String githubActions = System.getenv("GITHUB_ACTIONS");
                boolean isCI = githubActions != null && githubActions.equals("true");

                System.out.println("=== Driver Initialization ===");
                System.out.println("Running in CI: " + isCI);

                if (!isCI) {
                    System.out.println("Setting up ChromeDriver via WebDriverManager");
                    WebDriverManager.chromedriver().setup();
                } else {
                    System.out.println("Using pre-installed ChromeDriver from CI");
                    String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");
                    if (chromeDriverPath != null && !chromeDriverPath.isEmpty()) {
                        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                        System.out.println("ChromeDriver path set to: " + chromeDriverPath);
                    }
                }

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.addArguments("--disable-web-resources");
                options.addArguments("--disable-extensions");
                options.addArguments("--disable-plugins");
                options.addArguments("--disable-component-extensions-with-background-pages");
                options.setExperimentalOption("useAutomationExtension", false);
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("profile.default_content_setting_values.local_network_access", 2);
                options.setExperimentalOption("prefs", prefs);
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

                if (isCI) {
                    System.out.println("Configuring Chrome for CI environment");
                    String chromeBin = System.getenv("CHROME_BIN");
                    if (chromeBin != null && !chromeBin.isEmpty()) {
                        options.setBinary(chromeBin);
                        System.out.println("Chrome binary set to: " + chromeBin);
                    } else {
                        System.out.println("CHROME_BIN not set, using default");
                    }
                    options.addArguments("--headless=new");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-setuid-sandbox");
                    options.addArguments("--remote-debugging-port=9222");
                    options.addArguments("--disable-software-rasterizer");
                    options.addArguments("--remote-allow-origins=*");
                    System.out.println("Headless mode enabled with CI-specific options");
                } else {
                    options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                }

                try {
                    System.out.println("Creating ChromeDriver instance...");
                    webDriver = new ChromeDriver(options);
                    System.out.println("ChromeDriver created successfully!");
                } catch (Exception e) {
                    System.err.println("Failed to create ChromeDriver: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            case "firefox" -> {
                String githubActions = System.getenv("GITHUB_ACTIONS");
                boolean isCI = githubActions != null && githubActions.equals("true");

                System.out.println("=== Driver Initialization ===");
                System.out.println("Running in CI: " + isCI);

                if (!isCI) {
                    System.out.println("Setting up GeckoDriver via WebDriverManager");
                    WebDriverManager.firefoxdriver().setup();
                } else {
                    System.out.println("Using pre-installed GeckoDriver from CI");
                    String geckoDriverPath = System.getenv("GECKODRIVER_PATH");
                    if (geckoDriverPath != null && !geckoDriverPath.isEmpty()) {
                        System.setProperty("webdriver.gecko.driver", geckoDriverPath);
                        System.out.println("GeckoDriver path set to: " + geckoDriverPath);
                    }
                }

                FirefoxOptions options = new FirefoxOptions();
                options.addPreference("dom.webnotifications.enabled", false);
                options.addPreference("media.volume_scale", "0.0");
                options.addPreference("privacy.trackingprotection.enabled", false);

                if (isCI) {
                    System.out.println("Configuring Firefox for CI environment");
                    String firefoxBin = System.getenv("FIREFOX_BIN");
                    if (firefoxBin != null && !firefoxBin.isEmpty()) {
                        options.setBinary(firefoxBin);
                        System.out.println("Firefox binary set to: " + firefoxBin);
                    } else {
                        System.out.println("FIREFOX_BIN not set, using default");
                    }
                    options.addArguments("-headless");
                    options.addArguments("--width=1920");
                    options.addArguments("--height=1080");
                    System.out.println("Headless mode enabled with CI-specific options");
                } else {
                    options.addPreference("general.useragent.override",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Gecko/20100101 Firefox/120.0");
                }

                try {
                    System.out.println("Creating FirefoxDriver instance...");
                    webDriver = new FirefoxDriver(options);
                    System.out.println("FirefoxDriver created successfully!");
                } catch (Exception e) {
                    System.err.println("Failed to create FirefoxDriver: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        driver.set(webDriver);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

}