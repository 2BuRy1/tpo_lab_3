package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class FlipTextPage extends BasePage {

    private final By inputTextArea = By.xpath("(//textarea[@id='textData' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By outputTextArea = By.xpath("(//textarea[@id='textRes' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By flipButton = By.xpath("//*[='flipText']");

    public FlipTextPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public FlipTextPage open() {
        openRelative("/info/flip-text-online/");
        return this;
    }

    public FlipTextPage enterText(String text) {
        WebElement input = wait.until(d -> d.findElement(inputTextArea));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public FlipTextPage clickFlip() {
        wait.until(d -> d.findElement(flipButton)).click();
        return this;
    }

    public String getResultText() {
        return wait.until(d -> d.findElement(outputTextArea)).getAttribute("value");
    }

    public boolean isPageOpened() {
        return driver.findElement(inputTextArea).isDisplayed()
                && driver.findElement(outputTextArea).isDisplayed()
                && driver.findElement(flipButton).isDisplayed();
    }
}