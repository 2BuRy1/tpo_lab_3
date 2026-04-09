package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

public class RomiCalculatorPage extends BasePage {

    private final By profitInput = By.id("profit");
    private final By spendingInput = By.id("spending");
    private final By resultInput = By.id("res");
    private final By calcButton = By.id("calcROMI");

    public RomiCalculatorPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public RomiCalculatorPage open() {
        openRelative("/info/return-on-marketing-investment/");
        return this;
    }

    public RomiCalculatorPage setProfit(String value) {
        WebElement input = wait.until(d -> d.findElement(profitInput));
        input.clear();
        input.sendKeys(value);
        return this;
    }

    public RomiCalculatorPage setSpending(String value) {
        WebElement input = wait.until(d -> d.findElement(spendingInput));
        input.clear();
        input.sendKeys(value);
        return this;
    }

    public RomiCalculatorPage clickCalculate() {
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