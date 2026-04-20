package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class WhoisPage extends BasePage {

    private final By domainInput = By.id("url");
    private final By checkButton = By.cssSelector("button.submitCheckDomain");
    private final By resultBlock = By.cssSelector(".list > .list_item");

    public WhoisPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public WhoisPage open() {
        openRelative("/info/whois-proverka-domena/");
        return this;
    }

    public WhoisPage typeDomain(String domain) {
        WebElement input = wait.until(d -> d.findElement(domainInput));
        input.clear();
        input.sendKeys(domain);
        return this;
    }

    public WhoisPage submit() {
        wait.until(d -> d.findElement(checkButton)).click();
        return this;
    }

    public boolean hasWhoisResult() {
        return wait.until(d -> {
            for (WebElement el : d.findElements(resultBlock)) {
                String text = el.getText();
                if (text.contains("Domain Name:")
                        && text.contains("Creation Date:")
                        && text.contains("Registrar:")) {
                    return true;
                }
            }
            return false;
        });
    }

    public boolean containsDomain(String domain) {
        String expected = domain.toUpperCase();
        return wait.until(d -> {
            for (WebElement el : d.findElements(resultBlock)) {
                if (el.getText().contains("Domain Name: " + expected)) {
                    return true;
                }
            }
            return false;
        });
    }
}
