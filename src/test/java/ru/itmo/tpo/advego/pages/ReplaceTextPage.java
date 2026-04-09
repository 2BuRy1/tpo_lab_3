package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class ReplaceTextPage extends BasePage {

    private final By searchInput = By.xpath("(//textarea[@id='searchStr' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By replaceInput = By.xpath("(//textarea[@id='replaceStr' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By textInput = By.xpath("(//textarea[@id='textData' and not(contains(@style,'visibility: hidden'))])[1]");
    private final By replaceButton = By.id("replace");

    public ReplaceTextPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public ReplaceTextPage open() {
        openRelative("/info/replace-text-online/");
        return this;
    }

    public ReplaceTextPage enterSearchText(String text) {
        WebElement input = wait.until(d -> d.findElement(searchInput));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public ReplaceTextPage enterReplaceText(String text) {
        WebElement input = wait.until(d -> d.findElement(replaceInput));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public ReplaceTextPage enterSourceText(String text) {
        WebElement input = wait.until(d -> d.findElement(textInput));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public ReplaceTextPage clickReplace() {
        wait.until(d -> d.findElement(replaceButton)).click();
        return this;
    }

    public String getResultText() {
        return wait.until(d -> d.findElement(textInput)).getAttribute("value");
    }

    public boolean isPageOpened() {
        return driver.findElement(searchInput).isDisplayed()
                && driver.findElement(replaceInput).isDisplayed()
                && driver.findElement(textInput).isDisplayed()
                && driver.findElement(replaceButton).isDisplayed();
    }
}