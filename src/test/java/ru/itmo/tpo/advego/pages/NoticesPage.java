package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class NoticesPage extends BasePage {
    public NoticesPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public NoticesPage open() {
        openAuthorizedRelative("/notices/");
        wait.until(driver -> currentUrl().contains("/notices/")
                || count(xpath("//table[contains(@class,'notices')]")) > 0
                || pageSource().contains("notices"));
        return this;
    }

    public boolean hasHeading() {
        return count(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Извещ')]")) > 0
                || currentUrl().contains("/notices/");
    }

    public boolean hasNoticeTable() {
        return count(xpath("//table[contains(@class,'notices')]")) > 0;
    }

    public int noticesCount() {
        return count(xpath("//table[contains(@class,'notices')]//td[contains(@class,'notices_message')]"));
    }

    public boolean hasNonEmptyNoticeMessage() {
        return count(xpath("//table[contains(@class,'notices')]//td[contains(@class,'notices_message')][string-length(normalize-space()) > 10]")) > 0;
    }

    public boolean hasNoticeMetadata() {
        return count(xpath("//table[contains(@class,'notices')]//td[contains(@class,'notices_date') or contains(@class,'date') or contains(@class,'time')]")) > 0
                || count(xpath("//table[contains(@class,'notices')]//tr[.//td[contains(@class,'notices_message')] and .//td[2]]")) > 0;
    }
}
