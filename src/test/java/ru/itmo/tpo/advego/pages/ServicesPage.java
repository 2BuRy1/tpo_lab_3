package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class ServicesPage extends BasePage {
    public ServicesPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public ServicesPage open() {
        openRelative("/advego-services/");
        waitForVisible(xpath("//h1[contains(normalize-space(),'Сервисы Адвего')]"));
        return this;
    }

    public boolean hasHeading(String heading) {
        return isVisible(xpath("//h1[contains(normalize-space(),'" + heading + "')]"));
    }

    public String linkHref(String linkText) {
        return attribute(xpath("//a[normalize-space()='" + linkText + "']"), "href");
    }
}
