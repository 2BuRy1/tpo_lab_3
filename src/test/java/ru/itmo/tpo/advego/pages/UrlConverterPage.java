package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class UrlConverterPage extends BasePage {

    private final By inputText = By.cssSelector("#in_text");
    private final By outputText = By.cssSelector("#out_text");

    public UrlConverterPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public UrlConverterPage open() {
        openRelative("/yandex-translit-online/");
        return this;
    }

    public UrlConverterPage typeText(String text) {
        WebElement input = wait.until(d -> d.findElement(inputText));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public String getConvertedText() {
        return wait.until(d -> {
            String value = d.findElement(outputText).getAttribute("value");
            return value != null && !value.isBlank() ? value : null;
        });
    }

    public boolean isConvertedTo(String expected) {
        String actual = getConvertedText().trim();
        return actual.equals(expected);
    }

    public boolean outputIsNotEmpty() {
        return !getConvertedText().trim().isEmpty();
    }
}