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
        return isVisible(emailLocator());
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
        fill(emailLocator(), email);
        if (count(nicknameLocator()) > 0) {
            fill(nicknameLocator(), nickname);
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
        return xpath("(//form[contains(@action,'/join/') or @id='member-registration']//input[@id='join-form-email' or @name='email'])[1]");
    }

    private By passwordLocator() {
        return xpath("(//form[contains(@action,'/join/') or @id='member-registration']//input[@type='password' or @name='pwd' or @name='password'])[1]");
    }

    private By passwordConfirmLocator() {
        return xpath("(//form[contains(@action,'/join/') or @id='member-registration']//input[contains(@name,'confirm') or contains(@name,'repeat') or @name='pwd2'])[1]");
    }

    private By nicknameLocator() {
        return xpath("(//form[contains(@action,'/join/') or @id='member-registration']//input[@id='join-form-name' or @name='name'])[1]");
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
