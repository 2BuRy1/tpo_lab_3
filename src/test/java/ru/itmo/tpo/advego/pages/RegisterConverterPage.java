package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class RegisterConverterPage extends BasePage {
    public RegisterConverterPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public RegisterConverterPage open() {
        openRelative("/info/register-converter/");
        waitForVisible(xpath("//h1[contains(normalize-space(),'Конвертер регистров')]"));
        return this;
    }

    public void enterText(String text) {
        type(xpath("//form[contains(@class,'serviceButtons')]//textarea"), text);
    }

    public void toLowerCase() {
        String before = convertedText();
        click(xpath("//form[contains(@class,'serviceButtons')]//a[normalize-space()='нижний регистр']"));
        waitForValue(xpath("//form[contains(@class,'serviceButtons')]//textarea"), value -> !value.equals(before));
    }

    public void toUpperCase() {
        String before = convertedText();
        click(xpath("//form[contains(@class,'serviceButtons')]//a[normalize-space()='ВЕРХНИЙ РЕГИСТР']"));
        waitForValue(xpath("//form[contains(@class,'serviceButtons')]//textarea"), value -> !value.equals(before));
    }

    public void firstUpper() {
        String before = convertedText();
        click(xpath("//form[contains(@class,'serviceButtons')]//a[normalize-space()='Первая заглавная']"));
        waitForValue(xpath("//form[contains(@class,'serviceButtons')]//textarea"), value -> !value.equals(before));
    }

    public void allWordsFirstUpper() {
        String before = convertedText();
        click(xpath("//form[contains(@class,'serviceButtons')]//a[normalize-space()='Все Слова с Заглавной']"));
        waitForValue(xpath("//form[contains(@class,'serviceButtons')]//textarea"), value -> !value.equals(before));
    }

    public String convertedText() {
        return value(xpath("//form[contains(@class,'serviceButtons')]//textarea"));
    }
}
