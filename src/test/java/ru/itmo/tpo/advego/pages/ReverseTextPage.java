package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class ReverseTextPage extends BasePage {

    private final By textArea = By.xpath("(//textarea[@id='textData' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By reverseButton = By.xpath("//*[='reverseText']");

    public ReverseTextPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public ReverseTextPage open() {
        openRelative("/info/text-reverse/");
        return this;
    }

    public ReverseTextPage enterText(String text) {
        WebElement input = wait.until(d -> d.findElement(textArea));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public ReverseTextPage clickReverse() {
        wait.until(d -> d.findElement(reverseButton)).click();
        return this;
    }

    public String getText() {
        return wait.until(d -> d.findElement(textArea)).getAttribute("value");
    }

    public boolean isPageOpened() {
        return driver.findElement(textArea).isDisplayed()
                && driver.findElement(reverseButton).isDisplayed();
    }
}