package ru.itmo.tpo.advego.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.config.TestConfig;

public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.create(TestConfig.browser(), TestConfig.headless());
        if (!TestConfig.reuseSession()) {
            driver.manage().deleteAllCookies();
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
