package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.tpo.advego.core.BasePage;

public class SiteIksPage extends BasePage {

    private final By domainInput = By.xpath("//*[='url']");
    private final By checkButton = By.xpath("//*[='sqi_check_button']");
    private final By iksValue = By.xpath("//*[='sqi_value']");
    private final By resultUrlBlock = By.xpath("//*[='sqi_result_url_block']");
    private final By loading = By.xpath("//*[='common_loading']");

    public SiteIksPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public SiteIksPage open() {
        openRelative( "/info/opredelit-yandeks-iks-sita/");
        return this;
    }

    public SiteIksPage enterDomain(String domain) {
        WebElement input = wait.until(d -> d.findElement(domainInput));
        input.clear();
        input.sendKeys(domain);
        return this;
    }

    public SiteIksPage clickCheck() {
        wait.until(d -> d.findElement(checkButton)).click();
        return this;
    }

    public String getIksValue() {
        return wait.until(d -> d.findElement(iksValue)).getText().trim();
    }

    public String getDomainValue() {
        return wait.until(d -> d.findElement(domainInput)).getAttribute("value");
    }

    public boolean isPageOpened() {
        return wait.until(d -> d.findElement(domainInput)).isDisplayed()
                && wait.until(d -> d.findElement(checkButton)).isDisplayed();
    }

    public SiteIksPage waitForIksResult(Duration timeout) {
        WebDriverWait longWait = new WebDriverWait(driver, timeout);

        longWait.until(d -> {
            try {
                WebElement value = d.findElement(iksValue);
                String text = value.getText().trim();
                return value.isDisplayed() && !text.isBlank() && text.matches("\\d+");
            } catch (Exception e) {
                return false;
            }
        });

        return this;
    }

    public boolean isResultShown() {
        try {
            WebElement value = driver.findElement(iksValue);
            WebElement resultBlock = driver.findElement(resultUrlBlock);
            String text = value.getText().trim();

            return value.isDisplayed()
                    && resultBlock.isDisplayed()
                    && !text.isBlank()
                    && text.matches("\\d+");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoadingVisible() {
        try {
            return driver.findElement(loading).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}