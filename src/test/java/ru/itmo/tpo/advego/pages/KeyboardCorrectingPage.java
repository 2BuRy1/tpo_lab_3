package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class KeyboardCorrectingPage extends BasePage {
    public KeyboardCorrectingPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public KeyboardCorrectingPage open() {
        openRelative("/info/keyboard-correcting/");
        waitForVisible(xpath("//h1[contains(normalize-space(),'неправильной раскладке')]"));
        return this;
    }

    public void enterText(String text) {
        type(xpath("//form[contains(@class,'serviceButtons')]//textarea"), text);
    }

    public void convertToRussian() {
        String before = textValue();
        click(xpath("//form[contains(@class,'serviceButtons')]//a[normalize-space()='На русскую раскладку']"));
        waitForValue(xpath("//form[contains(@class,'serviceButtons')]//textarea"), value -> !value.equals(before));
    }

    public void convertToLatin() {
        String before = textValue();
        click(xpath("//form[contains(@class,'serviceButtons')]//a[normalize-space()='На латиницу']"));
        waitForValue(xpath("//form[contains(@class,'serviceButtons')]//textarea"), value -> !value.equals(before));
    }

    public String textValue() {
        return value(xpath("//form[contains(@class,'serviceButtons')]//textarea"));
    }
}
