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
import ru.itmo.tpo.advego.config.TestConfig;
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
        By loginModal = By.xpath("//div[@id='login_small' and not(contains(@style,'display: none'))]");

        if (count(captchaFrame) == 0) {
            return;
        }

        try {
            new WebDriverWait(driver, Duration.ofMinutes(3))
                    .until(driver -> {
                        boolean captchaStillVisible = driver.findElements(captchaFrame).stream().anyMatch(element -> {
                            try {
                                return element.isDisplayed();
                            } catch (Exception e) {
                                return false;
                            }
                        });

                        boolean modalStillVisible = driver.findElements(loginModal).stream().anyMatch(element -> {
                            try {
                                return element.isDisplayed();
                            } catch (Exception e) {
                                return false;
                            }
                        });

                        return !captchaStillVisible || !modalStillVisible || hasAuthorizedHeader();
                    });
        } catch (TimeoutException e) {
            throw new IllegalStateException("Капча не была решена вручную за отведенное время.");
        }
    }

    protected WebElement findVisible(By locator) {
        return driver.findElements(locator).stream()
                .filter(element -> {
                    try {
                        return element.isDisplayed();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Visible element not found: " + locator));
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
        if (hasAuthorizedHeader()) {
            return;
        }
        loginThroughModalStupidly();

        wait.until(driver ->
                currentUrlContainsPath(path)
                        || !isLoginModalVisible()
                        || hasAuthorizedHeader()
        );
    }

    protected void loginThroughModalStupidly() {
        By modal = By.xpath("//div[@id='login_small' and not(contains(@style,'display: none'))]");

        if (!waitForModalOrAuth(modal, Duration.ofSeconds(12))) {
            return;
        }
        if (hasAuthorizedHeader()) {
            return;
        }

        boolean modalVisible = isLoginModalVisible();
        By loginInput = modalVisible
                ? By.xpath("//form[@id='login_small_form' and not(contains(@style,'display: none'))]//input[@name='login']")
                : By.xpath("//form[@id='login_form' and not(contains(@style,'display: none'))]//input[@name='login']");
        By passwordInput = modalVisible
                ? By.xpath("//form[@id='login_small_form' and not(contains(@style,'display: none'))]//input[@name='pwd']")
                : By.xpath("//form[@id='login_form' and not(contains(@style,'display: none'))]//input[@name='pwd']");
        By submitButton = modalVisible
                ? By.xpath("//form[@id='login_small_form' and not(contains(@style,'display: none'))]//*[@id='login_ok' or (self::a and normalize-space()='Войти') or (self::button and normalize-space()='Войти')][1]")
                : By.xpath("//form[@id='login_form' and not(contains(@style,'display: none'))]//*[self::a or self::button][contains(normalize-space(),'Войти')][1]");

        // Step 1: email + submit (password can appear only after this click)
        WebElement loginField = waitForVisible(loginInput);
        WebElement submit = waitForVisible(submitButton);

        scrollIntoView(loginField);
        fillLoginField(loginInput, TestCredentials.login());
        ensureInputFilled(loginInput);

        if (!isFieldVisibleAndEnabled(passwordInput)) {
            clickSubmit(submit);
        }

        // Step 2: wait password field become active, then password + optional manual captcha pause + submit
        boolean passwordStepShown = wait.until(driver -> {
            List<WebElement> fields = driver.findElements(passwordInput);
            for (WebElement field : fields) {
                try {
                    if (field.isDisplayed() && field.isEnabled()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
            return hasAuthorizedHeader() || !isLoginModalVisible();
        });
        if (passwordStepShown && (hasAuthorizedHeader() || !isLoginModalVisible())) {
            return;
        }

        WebElement passwordField = waitForVisible(passwordInput);
        scrollIntoView(passwordField);
        fillPasswordField(passwordInput, TestCredentials.password(), modalVisible);
        ensurePasswordFilled(passwordInput, TestCredentials.password());
        ensureInputValueEquals(passwordInput, TestCredentials.password());

        pauseForManualCaptcha();
        clickSubmit(waitForVisible(submitButton));

        waitForManualCaptchaSolveIfPresent();
        wait.until(driver -> !isAnyLoginSurfaceVisible() || hasAuthorizedHeader() || currentUrl().contains("/user/"));
    }

    private void pauseForManualCaptcha() {
        long millis = TestConfig.authPasswordDelay().toMillis();
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void clickSubmit(WebElement submit) {
        try {
            submit.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
        }
    }

    private void fillPasswordField(By passwordInput, String password, boolean modalForm) {
        executeScript("""
                const selector = arguments[1] ? "#login_small_form input[name='pwd']" : "#login_form input[name='pwd']";
                const fields = Array.from(document.querySelectorAll(selector));
                const field = fields.find(el => el.offsetParent !== null) || fields[0];
                if (!field) {
                    return;
                }
                field.removeAttribute('disabled');
                field.focus();
                field.value = arguments[0];
                field.dispatchEvent(new Event('input', {bubbles: true}));
                field.dispatchEvent(new Event('change', {bubbles: true}));
                field.dispatchEvent(new Event('blur', {bubbles: true}));
                """, password, modalForm);
        // Fallback for drivers/sites where JS mutation is ignored until a native event occurs.
        WebElement field = waitForVisible(passwordInput);
        field.click();
        field.clear();
        field.sendKeys(password);
    }

    private void fillLoginField(By loginInput, String login) {
        executeScript("""
                const forms = Array.from(document.querySelectorAll("#login_small_form, #login_form"));
                const form = forms.find(el => el.offsetParent !== null) || forms[0];
                const field = form ? form.querySelector("input[name='login']") : null;
                if (!field) {
                    return;
                }
                field.removeAttribute('disabled');
                field.focus();
                field.value = arguments[0];
                field.dispatchEvent(new Event('input', {bubbles: true}));
                field.dispatchEvent(new Event('change', {bubbles: true}));
                field.dispatchEvent(new Event('blur', {bubbles: true}));
                """, login);
        WebElement field = waitForVisible(loginInput);
        field.click();
        field.clear();
        field.sendKeys(login);
    }

    private void ensureInputFilled(By input) {
        wait.until(driver -> driver.findElements(input).stream().anyMatch(field -> {
            try {
                String value = field.getDomProperty("value");
                if (value == null) {
                    value = field.getAttribute("value");
                }
                return value != null && !value.isBlank();
            } catch (Exception ex) {
                return false;
            }
        }));
    }

    private void ensurePasswordFilled(By passwordInput, String password) {
        ensureInputFilled(passwordInput);
    }

    private boolean isFieldVisibleAndEnabled(By locator) {
        return driver.findElements(locator).stream().anyMatch(field -> {
            try {
                return field.isDisplayed() && field.isEnabled();
            } catch (Exception ex) {
                return false;
            }
        });
    }

    private void ensureInputValueEquals(By input, String expected) {
        wait.until(driver -> driver.findElements(input).stream().anyMatch(field -> {
            try {
                String value = field.getDomProperty("value");
                if (value == null) {
                    value = field.getAttribute("value");
                }
                return expected.equals(value);
            } catch (Exception ex) {
                return false;
            }
        }));
    }

    private boolean waitForModalOrAuth(By modal, Duration timeout) {
        if (isAnyLoginSurfaceVisible() || hasAuthorizedHeader()) {
            return true;
        }
        triggerLoginModalIfPossible();
        try {
            new WebDriverWait(driver, timeout)
                    .until(driver -> isAnyLoginSurfaceVisible() || hasAuthorizedHeader());
        } catch (TimeoutException ignored) {
        }
        return isAnyLoginSurfaceVisible() || hasAuthorizedHeader();
    }

    private void triggerLoginModalIfPossible() {
        clickIfVisible(xpath("//a[@id='lets_work' and contains(normalize-space(),'Начать зарабатывать')]"));
        clickIfVisible(xpath("//a[contains(@class,'move_to_login') and (contains(@href,'/login') or contains(normalize-space(),'Вход'))]"));
        clickIfVisible(xpath("//a[contains(@href,'/login/') and contains(normalize-space(),'Вход')]"));
        clickIfVisible(xpath("//a[contains(@class,'login') and contains(normalize-space(),'Вход')]"));
    }

    protected boolean hasAuthorizedHeader() {
        return count(xpath("//a[contains(@href,'/logout/')]")) > 0;
    }

    private boolean isLoginModalVisible() {
        return driver.findElements(xpath("//div[@id='login_small']")).stream().anyMatch(element -> {
            try {
                return element.isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        });
    }

    private boolean isSideLoginFormVisible() {
        return driver.findElements(xpath("//form[@id='login_form']")).stream().anyMatch(element -> {
            try {
                return element.isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        });
    }

    private boolean isAnyLoginSurfaceVisible() {
        return isLoginModalVisible() || isSideLoginFormVisible();
    }

    private boolean currentUrlContainsPath(String path) {
        String target = path;
        int hashIndex = target.indexOf('#');
        if (hashIndex >= 0) {
            target = target.substring(0, hashIndex);
        }
        String current = currentUrl();
        if (current.contains(target)) {
            return true;
        }
        if (target.endsWith("/")) {
            return current.contains(target.substring(0, target.length() - 1));
        }
        return current.contains(target + "/");
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

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }
}
