package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class IpInfoPage extends BasePage {

    private final By content = By.cssSelector(".list_item");

    public IpInfoPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public IpInfoPage open() {
        openRelative("/info/uznat-svoj-ip-adres/");
        return this;
    }

    public boolean isOpened() {
        return wait.until(d -> d.findElement(content)).isDisplayed();
    }

    public String getPageText() {
        return wait.until(d -> d.findElement(content)).getText();
    }

    public boolean hasNonEmptyIp() {
        return hasNonEmptyValueAfterLabel("Ваш IP:");
    }

    public boolean hasNonEmptyLocation() {
        return hasNonEmptyValueAfterLabel("Местоположение:");
    }

    public boolean hasNonEmptyProviderAddress() {
        return hasNonEmptyValueAfterLabel("Адрес провайдера:");
    }

    private boolean hasNonEmptyValueAfterLabel(String label) {
        String text = getPageText();
        for (String line : text.split("\\R")) {
            if (line.startsWith(label)) {
                String value = line.substring(label.length()).trim();
                return !value.isBlank()
                        && !"null".equalsIgnoreCase(value)
                        && !"-".equals(value);
            }
        }
        return false;
    }
}