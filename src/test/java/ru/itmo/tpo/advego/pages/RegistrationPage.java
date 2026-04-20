package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.tpo.advego.core.BasePage;

public class RegistrationPage extends BasePage {
    private static final String DEFAULT_REG_PASSWORD = "AutotestPwd123!";

    public RegistrationPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public RegistrationPage open() {
        openRelative("/join/");
        if (isRegistrationPageLoaded()) {
            return this;
        }

        openRelative("/login/");
        try {
            String href = waitForVisible(registrationLinkLocator()).getAttribute("href");
            if (href != null && !href.isBlank()) {
                driver.get(href);
                waitForDocumentReady();
            }
        } catch (TimeoutException | NoSuchElementException ignored) {
        }

        wait.until(driver -> isRegistrationPageLoaded());
        return this;
    }

    public boolean hasRegistrationHeader() {
        return count(xpath("//h1[contains(normalize-space(),'Регистра')]")) > 0
                || count(xpath("//*[contains(normalize-space(),'Создать аккаунт')]")) > 0;
    }

    public boolean hasEmailInput() {
        return count(emailLocator()) > 0 || isVisible(emailLocator());
    }

    public boolean hasPasswordInput() {
        return isVisible(passwordLocator());
    }

    public boolean hasPasswordConfirmationInput() {
        return isVisible(passwordConfirmLocator());
    }

    public boolean hasRegisterButton() {
        return isVisible(registerButtonLocator());
    }

    public boolean hasNicknameInput() {
        return isVisible(nicknameLocator());
    }

    public RegistrationPage registerAs(String email, String nickname) {
        fillEmail(email);
        if (count(nicknameLocator()) > 0) {
            fillNickname(nickname);
        }
        if (count(passwordLocator()) > 0) {
            fillPassword(DEFAULT_REG_PASSWORD);
        }
        if (count(passwordConfirmLocator()) > 0) {
            fillPasswordConfirm(DEFAULT_REG_PASSWORD);
        }
        submitRegistration();
        return this;
    }

    public void waitForRegistrationAttemptResult() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(driver ->
                    hasAuthorizedHeader()
                            || hasRegistrationSuccessMessage()
                            || hasRegistrationErrorMessage()
                            || hasCaptchaChallenge()
            );
        } catch (TimeoutException ignored) {
            // Result is not always immediate due anti-bot/captcha behavior.
        }
    }

    public boolean hasRegistrationSuccessMessage() {
        return count(xpath("//*[contains(normalize-space(.),'аккаунт создан') or contains(normalize-space(.),'подтвердите e-mail') or contains(normalize-space(.),'подтвердите email')]")) > 0
                || currentUrl().contains("/register/success")
                || currentUrl().contains("/user/");
    }

    public boolean hasRegistrationErrorMessage() {
        return count(xpath("//*[contains(@class,'error') or contains(@class,'err')][contains(normalize-space(.),'уже') or contains(normalize-space(.),'некорр') or contains(normalize-space(.),'ошиб') or contains(normalize-space(.),'занят')]")) > 0
                || count(xpath("//*[contains(normalize-space(.),'Неверно заполнены')]")) > 0;
    }

    public boolean hasCaptchaChallenge() {
        return count(xpath("//iframe[contains(@src,'recaptcha')]")) > 0
                || count(xpath("//*[contains(@class,'captcha')]")) > 0;
    }

    public boolean isAuthorized() {
        return hasAuthorizedHeader();
    }

    private By emailLocator() {
        return xpath("(//input[@id='join-form-email' or @name='email' or @name='login'])[1]");
    }

    private By passwordLocator() {
        return xpath("(//input[@type='password' or @name='pwd' or @name='password' or contains(@id,'password')][1])");
    }

    private By passwordConfirmLocator() {
        return xpath("(//input[contains(@name,'confirm') or contains(@name,'repeat') or @name='pwd2' or contains(@id,'confirm')][1])");
    }

    private By nicknameLocator() {
        return xpath("(//input[@id='join-form-name' or @name='name'])[1]");
    }

    private By registerButtonLocator() {
        return xpath("(//form[contains(@action,'/join/') or @id='member-registration']//*[self::button or self::a][contains(normalize-space(),'Регистра') or contains(normalize-space(),'Создать')])[1]");
    }

    private By registrationLinkLocator() {
        return xpath("(//a[contains(@href,'register') or contains(normalize-space(),'Регистра')])[1]");
    }

    private void fill(By locator, String value) {
        WebElement element = waitForVisible(locator);
        scrollIntoView(element);
        element.click();
        element.clear();
        element.sendKeys(value);
    }

    private void fillEmail(String value) {
        if (!fillFirstAvailableInput(value,
                "#join-form-email",
                "input[name='email']",
                "input[name='login']")) {
            fill(emailLocator(), value);
        }
    }

    private void fillNickname(String value) {
        if (!fillFirstAvailableInput(value,
                "#join-form-name",
                "input[name='name']")) {
            fill(nicknameLocator(), value);
        }
    }

    private void fillPassword(String value) {
        if (!fillFirstAvailableInput(value,
                "#join-form-pwd",
                "input[name='pwd']",
                "input[name='password']",
                "input[type='password']")) {
            fill(passwordLocator(), value);
        }
    }

    private void fillPasswordConfirm(String value) {
        if (!fillFirstAvailableInput(value,
                "input[name='pwd2']",
                "input[name*='confirm']",
                "input[name*='repeat']",
                "input[id*='confirm']")) {
            fill(passwordConfirmLocator(), value);
        }
    }

    private boolean fillFirstAvailableInput(String value, String... selectors) {
        Object filled = executeScript("""
                const selectors = arguments[0];
                const value = arguments[1];
                let target = null;
                for (const selector of selectors) {
                    const all = Array.from(document.querySelectorAll(selector));
                    if (!all.length) continue;
                    target = all.find(el => el.offsetParent !== null && !el.disabled) || all.find(el => !el.disabled) || all[0];
                    if (target) break;
                }
                if (!target) return false;
                target.removeAttribute('disabled');
                target.focus();
                target.value = value;
                target.dispatchEvent(new Event('input', {bubbles: true}));
                target.dispatchEvent(new Event('change', {bubbles: true}));
                target.dispatchEvent(new Event('blur', {bubbles: true}));
                return true;
                """, selectors, value);
        return Boolean.TRUE.equals(filled);
    }

    private void submitRegistration() {
        Boolean clicked = (Boolean) executeScript("""
                const button = document.getElementById('join_ok');
                if (button) {
                    button.click();
                    return true;
                }
                return false;
                """);
        if (!Boolean.TRUE.equals(clicked)) {
            click(registerButtonLocator());
        }
    }

    private boolean isRegistrationPageLoaded() {
        return count(emailLocator()) > 0
                || count(passwordLocator()) > 0
                || count(registerButtonLocator()) > 0
                || hasRegistrationHeader();
    }
}
