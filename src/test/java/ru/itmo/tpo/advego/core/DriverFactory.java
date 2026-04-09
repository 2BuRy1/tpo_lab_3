package ru.itmo.tpo.advego.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import ru.itmo.tpo.advego.config.BrowserType;
import ru.itmo.tpo.advego.config.TestConfig;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver create(BrowserType browserType, boolean headless) {
        return switch (browserType) {
            case FIREFOX -> new FirefoxDriver(buildFirefoxOptions(headless));
            case CHROME -> new ChromeDriver(buildChromeOptions(headless));
        };
    }

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        existingBinary(
                "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
                "/Applications/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing"
        ).ifPresent(options::setBinary);
        options.addArguments("--window-size=1440,1200");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--lang=ru-RU");
        options.addArguments("--user-agent=" + TestConfig.userAgent());
        if (TestConfig.reuseSession()) {
            options.addArguments("--user-data-dir=" + TestConfig.browserProfileDir(BrowserType.CHROME));
        }
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        existingBinary(
                "/Applications/Firefox.app/Contents/MacOS/firefox",
                "/Applications/Firefox Developer Edition.app/Contents/MacOS/firefox"
        ).ifPresent(options::setBinary);
        options.addArguments("--width=1440");
        options.addArguments("--height=1200");
        options.addPreference("intl.accept_languages", "ru-RU,ru");
        options.addPreference("dom.webnotifications.enabled", false);
        if (TestConfig.reuseSession()) {
            options.addArguments("-profile");
            options.addArguments(TestConfig.browserProfileDir(BrowserType.FIREFOX).toString());
        }
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static java.util.Optional<String> existingBinary(String... candidates) {
        for (String candidate : candidates) {
            if (Files.isRegularFile(Path.of(candidate))) {
                return java.util.Optional.of(candidate);
            }
        }
        return java.util.Optional.empty();
    }
}
