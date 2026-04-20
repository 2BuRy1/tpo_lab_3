package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class LtvCalculatorPage extends BasePage {

    private final By aovInput = By.id("AOV");
    private final By rprInput = By.id("RPR");
    private final By lifetimeInput = By.id("Lifetime");
    private final By resultInput = By.id("res");
    private final By calcButton = By.id("calcCTR");

    public LtvCalculatorPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public LtvCalculatorPage open() {
        openRelative("/info/customer-lifetime-value/");
        return this;
    }

    public LtvCalculatorPage setAov(String value) {
        WebElement input = wait.until(d -> d.findElement(aovInput));
        input.clear();
        input.sendKeys(value);
        return this;
    }

    public LtvCalculatorPage setRpr(String value) {
        WebElement input = wait.until(d -> d.findElement(rprInput));
        input.clear();
        input.sendKeys(value);
        return this;
    }

    public LtvCalculatorPage setLifetime(String value) {
        WebElement input = wait.until(d -> d.findElement(lifetimeInput));
        input.clear();
        input.sendKeys(value);
        return this;
    }

    public LtvCalculatorPage clickCalculate() {
        wait.until(d -> d.findElement(calcButton)).click();
        return this;
    }

    public String getResult() {
        return wait.until(d -> {
            String value = d.findElement(resultInput).getAttribute("value");
            return value != null && !value.isBlank() ? value : null;
        });
    }

    public boolean isResultEqualTo(String expected) {
        return expected.equals(getResult().trim());
    }
}
