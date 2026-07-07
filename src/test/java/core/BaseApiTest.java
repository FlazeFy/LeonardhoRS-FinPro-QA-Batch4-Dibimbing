package core;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import java.util.Properties;

public class BaseApiTest {
    protected static Properties config;

    @BeforeSuite(alwaysRun = true)
    public void loadConfig() {
        String env = System.getProperty("env");
        env = (env == null || env.isEmpty()) ? "staging" : env;
        config = ConfigReader.loadProperties(env);
    }

    @BeforeClass
    public void setup() {
        String devBaseURL = config.getProperty("baseApiUrl");

        RestAssured.baseURI = devBaseURL;
    }
}
