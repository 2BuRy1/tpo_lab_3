package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BasePage;

public class OrderCreationPage extends BasePage {
    public OrderCreationPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public OrderCreationPage open() {
        openRelative("/order/add/#order-add", false);
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
        return count(xpath("//input[(@id='pwd' or @id='login-form-pwd' or @name='pwd') and not(@disabled)]")) > 0;
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
        return isDisplayedNow(xpath("//div[@id='email_required' and not(contains(@class,'off'))]"))
                || count(orderInlineEmailInput()) > 0;
    }

    public boolean isPasswordRequired() {
        return isDisplayedNow(xpath("//div[@id='password_required' and not(contains(@class,'off'))]"))
                || count(orderInlinePasswordInput()) > 0;
    }

    public OrderCreationPage enterLoginEmail(String email) {
        By strictInlineEmail = xpath("//div[@id='email_required' and not(contains(@class,'off'))]//input[@id='login' and not(@disabled)]");
        By locator = isDisplayedNow(strictInlineEmail) ? strictInlineEmail : orderInlineEmailInput();
        WebElement input = waitForVisible(locator);
        scrollIntoView(input);
        executeScript("""
                const container = document.querySelector("#email_required:not(.off)");
                const target = container ? container.querySelector("input#login, input[name='login']") : arguments[0];
                if (!target) {
                    return;
                }
                target.removeAttribute('disabled');
                target.focus();
                target.value = arguments[1];
                target.dispatchEvent(new Event('input', {bubbles: true}));
                target.dispatchEvent(new Event('change', {bubbles: true}));
                target.dispatchEvent(new Event('blur', {bubbles: true}));
                """, input, email);
        fillInputValue(locator, email);
        ensureInputValue(locator, email);
        return this;
    }

    public OrderCreationPage enterLoginPassword(String password) {
        By locator = orderInlinePasswordInput();
        WebElement input = waitForVisible(locator);
        scrollIntoView(input);
        scrollToPasswordField();
        fillInputValue(locator, password);
        ensureInputValue(locator, password);
        pauseForManualCaptcha();
        return this;
    }

    private void scrollToPasswordField() {
        executeScript("""
                const el = document.getElementById('login-form-pwd')
                    || document.getElementById('pwd')
                    || document.querySelector(\"input[name='pwd']\");
                if (el) {
                    el.scrollIntoView({block:'center'});
                }
                """);
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

    public OrderCreationPage saveOrderAfterAuth() {
        if (count(xpath("//div[@id='login_small' and not(contains(@style,'display: none'))]")) > 0) {
            click(xpath("(//form[@id='login_small_form' and not(contains(@style,'display: none'))]//*[@id='login_ok' or (self::a and normalize-space()='Войти') or (self::button and normalize-space()='Войти')][1])"));
            return this;
        }
        executeScript("""
                const submit = document.getElementById('order_add_submit');
                if (submit) {
                    submit.click();
                }
                """);
        return this;
    }

    public void saveOrderExpectingAuthOrInsufficientFunds() {
        executeScript("document.getElementById('order_add_submit').click();");
        wait.until(driver ->
                count(xpath("//div[@id='login_small' and not(contains(@style,'display: none'))]")) > 0
                        || isEmailRequired()
                        || isPasswordRequired()
                        || hasInsufficientFundsMessage()
                        || currentUrl().contains("/order/status/")
                        || count(xpath("//div[@id='order_saved' and not(contains(@style,'display: none'))]")) > 0
        );
        if (count(xpath("//div[@id='login_small' and not(contains(@style,'display: none'))]")) > 0) {
            loginThroughModalStupidly();
        }
    }

    public void startOrderExpectingAuthOrInsufficientFunds() {
        if (isEmailRequired() || isPasswordRequired()) {
            return;
        }
        clickVisibleStartOrSaveButton();
        wait.until(driver ->
                count(xpath("//div[@id='login_small' and not(contains(@style,'display: none'))]")) > 0
                        || isEmailRequired()
                        || isPasswordRequired()
                        || hasInsufficientFundsMessage()
                        || currentUrl().contains("/order/status/")
                        || count(xpath("//div[@id='order_saved' and not(contains(@style,'display: none'))]")) > 0
        );
        if (count(xpath("//div[@id='login_small' and not(contains(@style,'display: none'))]")) > 0) {
            loginThroughModalStupidly();
        }
    }

    public void authorizeInOrderFormIfRequired(String email, String password) {
        if (isEmailRequired()) {
            enterLoginEmail(email);
            saveOrderAfterAuth();
        }

        waitForAuthProgress();

        if (isPasswordRequired()) {
            enterLoginPassword(password);
            saveOrderAfterAuth();
        }
    }

    public void authorizeInOrderFormForStartIfRequired(String email, String password) {
        if (isEmailRequired()) {
            enterOrderStartEmailByStrictXpath(email);
            clickStartAfterInlineAuth();
            waitForAuthProgress();
            if (hasInlineInvalidEmailMessage() && isEmailRequired()) {
                enterOrderStartEmailByStrictXpath(email);
                clickStartAfterInlineAuth();
            }
        }

        waitForAuthProgress();

        if (isPasswordRequired()) {
            enterLoginPassword(password);
            clickStartAfterInlineAuth();
        }
    }

    public boolean hasInsufficientFundsMessage() {
        return count(xpath("//*[contains(normalize-space(.),'Для запуска заказа не хватает')]")) > 0
                && count(xpath("//*[contains(normalize-space(.),'пополнить счет') or contains(normalize-space(.),'пополнить счёт')]")) > 0;
    }

    public boolean hasInlineInvalidEmailMessage() {
        return isDisplayedNow(xpath("//div[@id='empty_login' and not(contains(@class,'off'))]"))
                || isDisplayedNow(xpath("//*[contains(normalize-space(.),'Укажите в поле действительный адрес почтового ящика') and not(contains(@style,'display:none'))]"));
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

    private void clickStartAfterInlineAuth() {
        clickVisibleStartOrSaveButton();
    }

    private void clickVisibleStartOrSaveButton() {
        By visibleStartButton = xpath("//a[@id='order_add_start_submit' and not(contains(@style,'display:none'))]");
        if (isDisplayedNow(visibleStartButton)) {
            click(visibleStartButton);
            return;
        }

        By visibleSaveButton = xpath("//a[@id='order_add_submit' and not(contains(@style,'display:none'))]");
        if (isDisplayedNow(visibleSaveButton)) {
            click(visibleSaveButton);
            return;
        }

        executeScript("""
                const visible = (el) => !!el && el.offsetParent !== null;
                const startButtons = Array.from(document.querySelectorAll('#order_add_start_submit'));
                const saveButtons = Array.from(document.querySelectorAll('#order_add_submit'));
                const button = startButtons.find(visible) || saveButtons.find(visible) || startButtons[0] || saveButtons[0];
                button?.click();
                """);
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

    private By orderInlineEmailInput() {
        return xpath("(" +
                "//div[@id='email_required' and not(contains(@class,'off'))]//input[@id='login' or @name='login']" +
                " | " +
                "//div[@id='email_required']//input[@id='login' or @name='login']" +
                ")" +
                "[not(@disabled)]" +
                "[not(@type='hidden')][1]");
    }

    private void enterOrderStartEmailByStrictXpath(String email) {
        By strictEmailLocator = xpath("//div[@id='email_required' and not(contains(@style,'display: none'))]//input[@id='login']");
        WebElement emailInput = waitForVisible(strictEmailLocator);
        scrollIntoView(emailInput);
        emailInput.click();
        emailInput.clear();
        emailInput.sendKeys(email);
        executeScript("""
                const input = arguments[0];
                const value = arguments[1];
                if (!input) return;
                input.removeAttribute('disabled');
                input.focus();
                input.value = value;
                input.dispatchEvent(new KeyboardEvent('keyup', {bubbles: true, key: 'g'}));
                input.dispatchEvent(new Event('input', {bubbles: true}));
                input.dispatchEvent(new Event('change', {bubbles: true}));
                input.dispatchEvent(new Event('blur', {bubbles: true}));
                """, emailInput, email);
        ensureInputValue(strictEmailLocator, email);
    }

    private By orderInlinePasswordInput() {
        return xpath("(" +
                "//div[@id='password_required']//input[@id='pwd' or @name='pwd']" +
                " | " +
                "//input[@id='login-form-pwd' or @id='pwd' or @name='pwd']" +
                ")" +
                "[not(@disabled)]" +
                "[not(@type='hidden')]" +
                "[not(ancestor::form[@id='login_small_form'])]" +
                "[not(ancestor::form[@action='/login/'])][1]");
    }

    private void fillInputValue(By locator, String value) {
        WebElement element = waitForVisible(locator);
        scrollIntoView(element);
        executeScript("""
                const target = arguments[0];
                const val = arguments[1];
                if (!target) {
                    return;
                }
                target.removeAttribute('disabled');
                target.focus();
                target.value = val;
                target.dispatchEvent(new Event('input', {bubbles: true}));
                target.dispatchEvent(new Event('change', {bubbles: true}));
                target.dispatchEvent(new Event('blur', {bubbles: true}));
                """, element, value);
        boolean valueSetByJs = driver.findElements(locator).stream().anyMatch(field -> {
            try {
                String current = field.getDomProperty("value");
                if (current == null) {
                    current = field.getAttribute("value");
                }
                return value.equals(current);
            } catch (Exception ex) {
                return false;
            }
        });
        if (!valueSetByJs) {
            element.click();
            element.clear();
            element.sendKeys(value);
        }
    }

    private void ensureInputValue(By locator, String expected) {
        wait.until(driver -> driver.findElements(locator).stream().anyMatch(field -> {
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

    private boolean isDisplayedNow(By locator) {
        return driver.findElements(locator).stream().anyMatch(element -> {
            try {
                return element.isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        });
    }

    private void waitForAuthProgress() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(driver ->
                    isPasswordRequired()
                            || hasInsufficientFundsMessage()
                            || hasSavedSuccessMessage()
            );
        } catch (TimeoutException ignored) {
            // Page may already contain an unchanged inline auth block; let caller continue and assert final state.
        }
    }
}
