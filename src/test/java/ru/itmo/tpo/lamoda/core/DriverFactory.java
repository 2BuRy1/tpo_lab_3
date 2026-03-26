package ru.itmo.tpo.lamoda.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import ru.itmo.tpo.lamoda.config.BrowserType;
import ru.itmo.tpo.lamoda.config.TestConfig;

import java.util.List;

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
        options.addArguments("--window-size=1440,1200");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--lang=ru-RU");
        options.addArguments("--user-agent=" + TestConfig.userAgent());
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1440");
        options.addArguments("--height=1200");
        options.addPreference("intl.accept_languages", "ru-RU,ru");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addPreference("general.useragent.override", TestConfig.userAgent());
        options.addPreference("dom.webnotifications.enabled", false);
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }
}
