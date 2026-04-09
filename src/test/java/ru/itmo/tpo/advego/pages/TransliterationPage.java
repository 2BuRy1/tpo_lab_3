package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class TransliterationPage extends BasePage {

    public TransliterationPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    private final By visibleInputTextArea = By.xpath("(//textarea[@id='in_text' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By visibleOutputTextArea = By.xpath("(//textarea[@id='out_text' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By translitHeader = By.xpath("//p/strong[normalize-space()='Транслит онлайн']");

    public TransliterationPage open() {
        openRelative( "/translit-online/");
        return this;
    }

    public boolean isPageOpened() {
        return isVisible(translitHeader) && isVisible(visibleInputTextArea) && isVisible(visibleOutputTextArea);
    }

    public TransliterationPage enterText(String text) {
        WebElement input = waitUntilVisible(visibleInputTextArea);
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public String getResultText() {
        return waitUntilVisible(visibleOutputTextArea).getAttribute("value");
    }

    public boolean isTransliteratedTo(String sourceText, String expectedText) {
        enterText(sourceText);
        return waitUntilVisible(visibleOutputTextArea)
                .getAttribute("value")
                .equals(expectedText);
    }

    private WebElement waitUntilVisible(By locator) {
        return wait.until(driver -> {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed() ? element : null;
        });
    }

    private String baseUrl() {
        return System.getProperty("baseUrl", "https://advego.com");
    }
}