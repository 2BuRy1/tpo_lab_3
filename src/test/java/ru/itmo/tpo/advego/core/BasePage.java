package ru.itmo.tpo.advego.core;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.tpo.advego.config.TestCredentials;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private final String baseUrl;

    protected BasePage(WebDriver driver, Duration timeout, String baseUrl) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
        this.baseUrl = baseUrl;
    }

    protected By xpath(String expression) {
        return By.xpath(expression);
    }

    protected void waitForManualCaptchaSolveIfPresent() {
        By captchaFrame = By.xpath("//iframe[contains(@src,'recaptcha')]");
        By loginInput = By.id("login-form-login");

        if (count(captchaFrame) == 0) {
            return;
        }

        try {
            new WebDriverWait(driver, Duration.ofMinutes(2))
                    .until(driver -> {
                        boolean captchaStillVisible = driver.findElements(captchaFrame).stream().anyMatch(element -> {
                            try {
                                return element.isDisplayed();
                            } catch (Exception e) {
                                return false;
                            }
                        });

                        boolean loginStillVisible = driver.findElements(loginInput).stream().anyMatch(element -> {
                            try {
                                return element.isDisplayed();
                            } catch (Exception e) {
                                return false;
                            }
                        });

                        return !captchaStillVisible || !loginStillVisible;
                    });
        } catch (TimeoutException e) {
            throw new IllegalStateException("Капча не была решена вручную за отведенное время.");
        }
    }

    protected void openRelative(String path) {
        openRelative(path, true);
    }

    protected void openRelative(String path, boolean dismissOverlays) {
        driver.get(baseUrl + path);
        waitForDocumentReady();
        if (dismissOverlays) {
            dismissOverlays();
        }
    }

    protected void openAuthorizedRelative(String path) {
        openAuthorizedRelative(path, true);
    }

    protected void openAuthorizedRelative(String path, boolean dismissOverlays) {
        openRelative(path, dismissOverlays);
        loginThroughModalStupidly();

        wait.until(driver ->
                currentUrl().contains(path)
                        || count(By.id("login_small")) == 0
        );
    }

    protected void loginThroughModalStupidly() {
        By loginInput = By.id("login-form-login");
        By passwordInput = By.id("login-form-pwd");
        By submitButton = By.id("login_ok");

        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(loginInput));
        } catch (TimeoutException e) {
            return;
        }

        WebElement loginField = driver.findElement(loginInput);
        WebElement passwordField = driver.findElement(passwordInput);
        WebElement submit = driver.findElement(submitButton);

        scrollIntoView(loginField);
        loginField.click();
        loginField.clear();
        loginField.sendKeys(TestCredentials.login());

        scrollIntoView(passwordField);
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(TestCredentials.password());

        try {
            submit.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
        }

        waitForManualCaptchaSolveIfPresent();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(driver -> {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return element;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
            return null;
        });
    }

    protected WebElement waitForPresent(By locator) {
        return wait.until(driver -> driver.findElements(locator).stream().findFirst().orElse(null));
    }

    protected boolean isVisible(By locator) {
        try {
            waitForVisible(locator);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    protected void click(By locator) {
        WebElement element = waitForVisible(locator);
        scrollIntoView(element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void type(By locator, String value) {
        WebElement element = waitForVisible(locator);
        scrollIntoView(element);
        element.clear();
        element.sendKeys(value);
    }

    protected void setCheckbox(By locator, boolean checked) {
        WebElement checkbox = wait.until(driver -> driver.findElements(locator).stream().findFirst().orElse(null));
        if (checkbox.isSelected() != checked) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        }
    }

    protected void selectByVisibleText(By locator, String text) {
        new Select(waitForVisible(locator)).selectByVisibleText(text);
    }

    protected String text(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected String value(By locator) {
        WebElement element = waitForVisible(locator);
        String domValue = element.getDomProperty("value");
        return domValue == null ? element.getAttribute("value") : domValue;
    }

    protected String selectedOptionText(By locator) {
        return new Select(waitForVisible(locator)).getFirstSelectedOption().getText().trim();
    }

    protected String attribute(By locator, String name) {
        return waitForVisible(locator).getAttribute(name);
    }

    protected String attributeOfPresent(By locator, String name) {
        return waitForPresent(locator).getAttribute(name);
    }

    protected boolean isChecked(By locator) {
        return waitForVisible(locator).isSelected();
    }

    protected boolean isDisabled(By locator) {
        WebElement element = waitForVisible(locator);
        return !element.isEnabled() || element.getAttribute("disabled") != null;
    }

    protected boolean isDisabledPresent(By locator) {
        WebElement element = waitForPresent(locator);
        return !element.isEnabled() || element.getAttribute("disabled") != null;
    }

    protected int count(By locator) {
        return driver.findElements(locator).size();
    }

    protected void waitForText(By locator, Predicate<String> predicate) {
        wait.until(driver -> predicate.test(text(locator)));
    }

    protected void waitForValue(By locator, Predicate<String> predicate) {
        wait.until(driver -> predicate.test(value(locator)));
    }

    protected void waitForUrl(Predicate<String> predicate) {
        wait.until(driver -> predicate.test(driver.getCurrentUrl()));
    }

    protected String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected String pageSource() {
        return driver.getPageSource();
    }

    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    protected void waitForScriptCondition(String script) {
        wait.until(driver -> Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(script)));
    }

    protected void waitForDocumentReady() {
        wait.until((ExpectedCondition<Boolean>) driver ->
                "complete".equals(((JavascriptExecutor) driver).executeScript("return document.readyState")));
    }

    protected void dismissOverlays() {
        clickIfVisible(xpath("//button[contains(normalize-space(),'Закрыть')]"));
        clickIfVisible(xpath("//button[@aria-label='Закрыть']"));
        clickIfVisible(xpath("//a[normalize-space()='Понятно']"));
    }

    protected void clickIfVisible(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        for (WebElement element : elements) {
            try {
                if (element.isDisplayed()) {
                    scrollIntoView(element);
                    element.click();
                    return;
                }
            } catch (Exception ignored) {
                return;
            }
        }
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }
}