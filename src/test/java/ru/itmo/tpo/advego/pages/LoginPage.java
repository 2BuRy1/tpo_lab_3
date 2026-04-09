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
        type(xpath("(//form[@action='/login/' and contains(@class,'action_form')]//input[@name='login'])[1]"), email);
        type(xpath("(//form[@action='/login/' and contains(@class,'action_form')]//input[@name='pwd'])[1]"), password);
        click(xpath("(//form[@action='/login/' and contains(@class,'action_form')]//*[self::a or self::button][normalize-space()='Войти'])[1]"));
        return this;
    }

    public void waitForAuthorizedHeader() {
        waitForVisible(authorizedHeaderLocator());
    }

    public void waitForAuthorizedHeader(Duration timeout) {
        new WebDriverWait(driver, timeout).until(driver -> hasAuthorizedHeader());
    }

    public boolean hasAuthorizedHeader() {
        return isVisible(authorizedHeaderLocator());
    }

    public boolean isStillOnLoginForm() {
        return isVisible(loginForm());
    }

    public boolean hasLoginError() {
        return isVisible(xpath("//*[contains(@class,'error') and (contains(normalize-space(),'Невер') or contains(normalize-space(),'ошиб'))]"));
    }

    private org.openqa.selenium.By loginForm() {
        return xpath("//form[@action='/login/' and contains(@class,'action_form')]//input[@name='login']");
    }

    private org.openqa.selenium.By authorizedHeaderLocator() {
        return xpath("//a[contains(@href,'/logout/') or contains(@href,'/user/') or contains(@href,'/money/add/')]");
    }
}
