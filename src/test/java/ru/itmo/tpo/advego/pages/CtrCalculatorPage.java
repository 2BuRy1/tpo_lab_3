package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class CtrCalculatorPage extends BasePage {
    public CtrCalculatorPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public CtrCalculatorPage open() {
        openRelative("/info/ctr-kalkulyator/");
        waitForVisible(xpath("//h1[contains(normalize-space(),'Калькулятор CTR')]"));
        return this;
    }

    public void enterShows(String shows) {
        type(xpath("//label[contains(normalize-space(),'Количество показов')]/following-sibling::input[1]"), shows);
    }

    public void enterClicks(String clicks) {
        type(xpath("//label[contains(normalize-space(),'Количество кликов')]/following-sibling::input[1]"), clicks);
    }

    public void calculate() {
        click(xpath("//a[normalize-space()='Посчитать CTR']"));
        waitForValue(xpath("//label[contains(normalize-space(),'Результат')]/following-sibling::input[1]"), value -> !value.isBlank());
    }

    public String result() {
        return value(xpath("//label[contains(normalize-space(),'Результат')]/following-sibling::input[1]"));
    }
}
