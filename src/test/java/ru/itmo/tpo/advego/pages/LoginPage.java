package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.tpo.advego.core.BasePage;

public class LoginPage extends BasePage {
    public LoginPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public LoginPage open() {
        openRelative("/login/");
        wait.until(driver -> driver.findElements(loginForm()).stream().anyMatch(element -> {
            try {
                return element.isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        }) || hasAuthorizedHeader());
        return this;
    }

    public LoginPage loginAs(String email, String password) {
        fillVisibleLoginForm(email, password);
        submitVisibleLoginForm();
        return this;
    }

    public void waitForAuthorizedHeader() {
        waitForVisible(authorizedHeaderLocator());
    }

    public void waitForAuthorizedHeader(Duration timeout) {
        new WebDriverWait(driver, timeout).until(driver -> hasAuthorizedHeader());
    }

    public void waitForLoginAttemptResult(Duration timeout) {
        new WebDriverWait(driver, timeout).until(driver ->
                hasAuthorizedHeader() || hasCaptchaChallenge() || hasLoginError());
    }

    public boolean hasAuthorizedHeader() {
        return isVisible(authorizedHeaderLocator());
    }

    public boolean isStillOnLoginForm() {
        return driver.findElements(loginForm()).stream().anyMatch(element -> {
            try {
                return element.isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        });
    }

    public boolean hasLoginError() {
        return isVisible(xpath("//*[contains(@class,'error') and (contains(normalize-space(),'Невер') or contains(normalize-space(),'ошиб'))]"));
    }

    public boolean hasCaptchaChallenge() {
        return count(xpath("//iframe[contains(@src,'recaptcha')]")) > 0
                || count(xpath("//*[contains(@class,'captcha')]")) > 0;
    }

    private org.openqa.selenium.By loginForm() {
        return xpath("//form[(@id='login_small_form' or @id='login_form' or @action='/login/') and not(contains(@style,'display: none'))]//input[@name='login' or @id='login-form-login']");
    }

    private org.openqa.selenium.By authorizedHeaderLocator() {
        return xpath("//a[contains(@href,'/logout/')]");
    }

    private void fillVisibleLoginForm(String email, String password) {
        Object ok = executeScript("""
                const forms = Array.from(document.querySelectorAll("#login_small_form, #login_form, form[action='/login/']"));
                const visibleForm = forms.find(f => f.offsetParent !== null && !String(f.getAttribute('style') || '').includes('display: none'))
                    || forms[0];
                if (!visibleForm) return false;

                const login = visibleForm.querySelector("#login-form-login, input[name='login']");
                const pwd = visibleForm.querySelector("#login-form-pwd, input[name='pwd']");
                if (!login || !pwd) return false;

                for (const [el, value] of [[login, arguments[0]], [pwd, arguments[1]]]) {
                    el.removeAttribute('disabled');
                    el.focus();
                    el.value = value;
                    el.dispatchEvent(new Event('input', {bubbles: true}));
                    el.dispatchEvent(new Event('change', {bubbles: true}));
                    el.dispatchEvent(new Event('blur', {bubbles: true}));
                }
                return true;
                """, email, password);
        if (!Boolean.TRUE.equals(ok)) {
            type(xpath("(//form[@id='login_small_form' and not(contains(@style,'display: none'))]//input[@id='login-form-login' or @name='login'] | //form[@id='login_form' and not(contains(@style,'display: none'))]//input[@name='login'])[1]"), email);
            type(xpath("(//form[@id='login_small_form' and not(contains(@style,'display: none'))]//input[@id='login-form-pwd' or @name='pwd'] | //form[@id='login_form' and not(contains(@style,'display: none'))]//input[@name='pwd'])[1]"), password);
        }
    }

    private void submitVisibleLoginForm() {
        Object submitted = executeScript("""
                const forms = Array.from(document.querySelectorAll("#login_small_form, #login_form, form[action='/login/']"));
                const visibleForm = forms.find(f => f.offsetParent !== null && !String(f.getAttribute('style') || '').includes('display: none'))
                    || forms[0];
                if (!visibleForm) return false;

                const loginBtn = visibleForm.querySelector("#login_ok, a[href='/login/'], button[type='submit']");
                if (loginBtn) {
                    loginBtn.click();
                }
                if (typeof visibleForm.requestSubmit === 'function') {
                    visibleForm.requestSubmit();
                } else {
                    visibleForm.submit();
                }
                return true;
                """);
        if (!Boolean.TRUE.equals(submitted)) {
            click(xpath("(//form[@id='login_small_form' and not(contains(@style,'display: none'))]//*[@id='login_ok' or (self::a and contains(normalize-space(),'Войти')) or (self::button and contains(normalize-space(),'Войти'))] | //form[@id='login_form' and not(contains(@style,'display: none'))]//*[self::a or self::button][contains(normalize-space(),'Войти')])[1]"));
        }
    }
}
