package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class MoneyOutPage extends BasePage {
    public MoneyOutPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public MoneyOutPage open() {
        openAuthorizedRelative("/money/out/");
        wait.until(driver -> currentUrl().contains("/money/out/")
                || pageSource().toLowerCase().contains("вывод")
                || pageSource().toLowerCase().contains("вывести"));
        return this;
    }

    public boolean hasHeading() {
        return count(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Вывод') or contains(normalize-space(),'Вывести')]")) > 0
                || pageSource().toLowerCase().contains("вывод средств");
    }

    public boolean hasCardWithdrawalOption() {
        return count(xpath("//a[contains(@class,'mir') or contains(@class,'card') or contains(normalize-space(),'карт')]"
                + " | //label[contains(normalize-space(),'карт')]"
                + " | //input[contains(@name,'card') or contains(@id,'card')]")) > 0
                || pageSource().toLowerCase().contains("карт");
    }

    public boolean hasAmountField() {
        return count(xpath("//input[(@type='text' or @type='number') and "
                + "(contains(@name,'sum') or contains(@name,'amount') or contains(@id,'sum') or contains(@id,'amount'))]"
                + " | //label[contains(normalize-space(),'Сумм') or contains(normalize-space(),'сумм')]")) > 0
                || pageSource().toLowerCase().contains("сумм");
    }

    public boolean hasWithdrawSubmitAction() {
        return count(xpath("//a[contains(@id,'money_out') or contains(normalize-space(),'Вывести') or contains(normalize-space(),'Отправить')]"
                + " | //button[contains(normalize-space(),'Вывести') or contains(normalize-space(),'Отправить')]"
                + " | //input[@type='submit']")) > 0;
    }
}
