package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class OrderCreationPage extends BasePage {
    public OrderCreationPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public OrderCreationPage open() {
        openAuthorizedRelative("/order/add/#order-add", false);
        wait.until(driver -> pageSource().contains("order_add_form")
                || count(xpath("//div[@id='bl-1']//h3[contains(normalize-space(),'Название и тематика')]")) > 0
                || currentUrl().contains("/order/add/#order-add"));
        if (!pageSource().contains("order_add_form")
                && count(xpath("//div[@id='bl-1']//h3[contains(normalize-space(),'Название и тематика')]")) == 0) {
            throw new IllegalStateException("Форма создания заказа не появилась после перехода: " + currentUrl());
        }
        return this;
    }

    public boolean isPasswordInputEnabled() {
        return count(xpath("//input[@id='pwd' and not(@disabled)]")) > 0;
    }

    public void waitForEnabledPasswordInput() {
        wait.until(driver -> isPasswordInputEnabled());
    }

    public boolean hasNameAndCategoryBlock() {
        return isVisible(xpath("//div[@id='bl-1']//h3[contains(normalize-space(),'Название и тематика')]"));
    }

    public boolean hasDescriptionBlock() {
        return isVisible(xpath("//div[@id='bl-2']//h3[contains(normalize-space(),'Описание заказа')]"));
    }

    public boolean isEmailRequired() {
        return isVisible(xpath("//div[@id='email_required' and not(contains(@class,'off'))]"))
                || isVisible(xpath("//input[@id='login']"));
    }

    public boolean isPasswordRequired() {
        return isVisible(xpath("//div[@id='password_required' and not(contains(@class,'off'))]"))
                || isVisible(xpath("//input[@id='pwd' and not(@disabled)]"));
    }

    public OrderCreationPage enterLoginEmail(String email) {
        waitForVisible(xpath("//input[@id='login']"));
        setValueById("login", email);
        return this;
    }

    public OrderCreationPage enterLoginPassword(String password) {
        WebElement input = waitForVisible(xpath("//input[@id='pwd' and not(@disabled)]"));
        scrollToPasswordField();
        setValueById("pwd", password);
        return this;
    }

    private void scrollToPasswordField() {
        executeScript("document.getElementById('pwd').scrollIntoView({block:'center'});");
    }

    public OrderCreationPage saveOrderAfterAuth() {
        click(xpath("//a[@id='order_add_submit' and not(contains(@class,'disabled'))]"));
        return this;
    }

    public void saveOrderExpectingAuthOrInsufficientFunds() {
        executeScript("document.getElementById('order_add_submit').click();");
        wait.until(driver ->
                isEmailRequired()
                        || isPasswordRequired()
                        || hasInsufficientFundsMessage()
                        || currentUrl().contains("/order/status/")
                        || count(xpath("//div[@id='order_saved' and not(contains(@style,'display: none'))]")) > 0
        );
    }

    public boolean hasInsufficientFundsMessage() {
        return count(xpath("//*[contains(normalize-space(.),'Для запуска заказа не хватает')]")) > 0
                && count(xpath("//*[contains(normalize-space(.),'пополнить счет') or contains(normalize-space(.),'пополнить счёт')]")) > 0;
    }

    public String insufficientFundsText() {
        return text(xpath("//*[contains(normalize-space(.),'Для запуска заказа не хватает')]"));
    }

    public String defaultCategory() {
        return selectedOptionText(xpath("//select[@id='id_cat']"));
    }

    public String defaultLanguage() {
        return selectedOptionText(xpath("//select[@id='id_lang']"));
    }

    public String descriptionTemplate() {
        return value(xpath("//textarea[@id='order_desc_src']"));
    }

    public String jobsCount() {
        return text(xpath("//em[contains(@class,'jobs_count')]"));
    }

    public String totalBudget() {
        return text(xpath("//span[@id='total_price_order']"));
    }

    public OrderCreationPage enterTitle(String title) {
        setValueById("order_title", title);
        return this;
    }

    public OrderCreationPage selectOrderType(String orderType) {
        selectByVisibleTextUsingJs("order_type", orderType);
        return this;
    }

    public OrderCreationPage selectTextType(String textType) {
        selectByVisibleTextUsingJs("order_text_type", textType);
        return this;
    }

    public OrderCreationPage enterDescription(String description) {
        setValueById("order_desc_src", description);
        return this;
    }

    public OrderCreationPage enterCost(String cost) {
        setValueById("text_cost", cost);
        return this;
    }

    public void submitEmptyForm() {
        executeScript("document.getElementById('order_add_submit').click();");
        waitForVisible(xpath("//div[@id='check_empty_fields' and not(contains(@style,'display:none'))]"));
    }

    public void saveOrder() {
        saveOrderExpectingAuthOrInsufficientFunds();
    }

    public boolean hasSavedSuccessMessage() {
        return count(xpath("//div[@id='order_saved']//h3[contains(.,'сохран')]")) > 0
                || currentUrl().contains("/order/status/");
    }

    public String savedOrderCardHref() {
        if (count(xpath("//div[@id='order_saved']//a[contains(@class,'btn-status')]")) > 0) {
            return attribute(xpath("//div[@id='order_saved']//a[contains(@class,'btn-status')]"), "href");
        }
        return currentUrl();
    }

    public boolean hasRequiredFieldsError() {
        return isVisible(xpath("//div[@id='check_empty_fields'][contains(.,'Заполните выделенные поля')]"));
    }

    public boolean hasMissingOrderTypeError() {
        return isVisible(xpath("//div[contains(@class,'ot')][contains(.,'тип работы')]"));
    }

    public boolean hasMissingTextTypeError() {
        return isVisible(xpath("//div[contains(@class,'ott')][contains(.,'тип текста')]"));
    }

    private void setValueById(String id, String value) {
        Boolean updated = (Boolean) executeScript("""
            const element = document.getElementById(arguments[0]);
            if (!element) {
                return false;
            }
            element.removeAttribute('disabled');
            element.focus();
            element.value = arguments[1];
            element.dispatchEvent(new Event('input', {bubbles: true}));
            element.dispatchEvent(new Event('change', {bubbles: true}));
            element.dispatchEvent(new Event('blur', {bubbles: true}));
            return true;
            """, id, value);
        if (!Boolean.TRUE.equals(updated)) {
            throw new IllegalStateException("Не удалось установить значение для поля " + id + ".");
        }
    }

    private void selectByVisibleTextUsingJs(String id, String text) {
        Boolean updated = (Boolean) executeScript("""
                const select = document.getElementById(arguments[0]);
                if (!select) {
                    return false;
                }
                const requested = arguments[1].trim().toLowerCase();
                const option = Array.from(select.options).find(item => item.text.trim().toLowerCase() === requested)
                    || Array.from(select.options).find(item => item.text.trim().toLowerCase().includes(requested));
                if (!option) {
                    return false;
                }
                select.value = option.value;
                select.dispatchEvent(new Event('change', {bubbles: true}));
                return true;
                """, id, text);
        if (!Boolean.TRUE.equals(updated)) {
            throw new IllegalStateException("Не удалось выбрать значение '" + text + "' в списке " + id + ".");
        }
    }
}
