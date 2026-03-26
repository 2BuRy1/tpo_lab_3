package ru.itmo.tpo.lamoda.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.lamoda.config.TestConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.create(TestConfig.browser(), TestConfig.headless());
        Map<String, Object> params = new HashMap<>();
        params.put("source", "Object.defineProperty(navigator, 'webdriver', { get: () => undefined })");
        //driver.get("Page.addScriptToEvaluateOnNewDocument", params);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
