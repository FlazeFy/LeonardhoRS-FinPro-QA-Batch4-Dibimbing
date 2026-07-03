plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.testng:testng:7.10.2")
    implementation("org.seleniumhq.selenium:selenium-java:4.35.0")
    implementation("io.github.bonigarcia:webdrivermanager:6.3.2")
    testImplementation("org.assertj:assertj-core:3.27.3")
    implementation("org.apache.poi:poi:5.4.1")
    implementation("com.aventstack:extentreports:5.1.2")
    implementation("org.apache.poi:poi-ooxml:5.4.1")
    implementation("org.apache.logging.log4j:log4j-core:2.25.1")
    implementation("org.apache.logging.log4j:log4j-api:2.25.1")
    implementation("commons-io:commons-io:2.15.1")
}


tasks.test {
    useTestNG {
        val suite: String = if (project.hasProperty("suite")) {
            project.property("suite") as String
        } else {
            "smoke.xml"
        }
        println("Run test suite: $suite")
        suiteXmlFiles = listOf(file("src/test/resources/suites/$suite"))
        if (project.hasProperty("env")) {
            systemProperty("env", project.property("env") as String)
        }
    }
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
