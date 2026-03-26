package ru.itmo.tpo.lamoda.core;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private final Duration timeout;
    private final String baseUrl;

    protected BasePage(WebDriver driver, Duration timeout, String baseUrl) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
        this.timeout = timeout;
        this.baseUrl = baseUrl;
    }

    protected void openRelative(String path) {
        driver.get(baseUrl + path);
        waitForDocumentReady();
        dismissOverlays();
    }

    protected By xpath(String value) {
        return By.xpath(value);
    }

    protected WebElement waitForVisible(By... locators) {
        try {
            return wait.until(webDriver -> {
                for (By locator : locators) {
                    List<WebElement> elements = webDriver.findElements(locator);
                    for (WebElement element : elements) {
                        try {
                            if (element.isDisplayed()) {
                                return element;
                            }
                        } catch (StaleElementReferenceException ignored) {
                        }
                    }
                }
                return null;
            });
        } catch (TimeoutException ex) {
            if (isAntiBotPage()) {
                throw new AssertionError("Lamoda returned a Qrator anti-bot page instead of the target page. Run the tests in a trusted non-blocked browser environment.", ex);
            }
            throw ex;
        }
    }

    protected boolean isVisible(By... locators) {
        try {
            waitForVisible(locators);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    protected int countVisible(By... locators) {
        return (int) Arrays.stream(locators)
                .flatMap(locator -> driver.findElements(locator).stream())
                .filter(element -> {
                    try {
                        return element.isDisplayed();
                    } catch (StaleElementReferenceException ex) {
                        return false;
                    }
                })
                .count();
    }

    protected void click(By... locators) {
        WebElement element = waitForVisible(locators);
        scrollIntoView(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        try {
            element.click();
        } catch (Exception ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void typeAndSubmit(String value, By... locators) {
        WebElement element = waitForVisible(locators);
        element.clear();
        element.sendKeys(value);
        element.sendKeys(Keys.ENTER);
    }

    protected String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected Duration timeout() {
        return timeout;
    }

    protected String baseUrl() {
        return baseUrl;
    }

    protected void waitForDocumentReady() {
        wait.until((ExpectedCondition<Boolean>) webDriver ->
                "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }

    protected void dismissOverlays() {
        clickIfPresent(
                xpath("//button[contains(.,'Принять')]"),
                xpath("//button[contains(.,'Соглас')]"),
                xpath("//button[contains(.,'Понятно')]"),
                xpath("//button[@aria-label='Закрыть']"),
                xpath("//button[contains(.,'Закрыть')]")
        );
    }

    protected boolean isAntiBotPage() {
        String pageSource = driver.getPageSource();
        String currentUrl = driver.getCurrentUrl();
        return pageSource.contains("/__qrator/qauth.js")
                || pageSource.toLowerCase().contains("qrator")
                || currentUrl.contains("__qrator");
    }

    protected void clickIfPresent(By... locators) {
        for (By locator : locators) {
            try {
                List<WebElement> elements = driver.findElements(locator);
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        scrollIntoView(element);
                        element.click();
                        return;
                    }
                }
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            } catch (Exception ex) {
                return;
            }
        }
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }
}
