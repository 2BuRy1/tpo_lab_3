package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class ExecutorStatsPage extends BasePage {
    public ExecutorStatsPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public ExecutorStatsPage open() {
        openAuthorizedRelative("/stat/author/");
        wait.until(driver -> currentUrl().contains("/stat/author/")
                || pageSource().toLowerCase().contains("статист")
                || pageSource().toLowerCase().contains("доход"));
        return this;
    }

    public boolean hasHeading() {
        return count(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Статист') or contains(normalize-space(),'Доход')]")) > 0
                || pageSource().toLowerCase().contains("статист");
    }

    public boolean hasStatsTableOrBlocks() {
        return count(xpath("//table[contains(@class,'stat') or contains(@class,'table')]"
                + " | //div[contains(@class,'stat') or contains(@id,'stat')]")) > 0;
    }

    public boolean hasPeriodOrDateFilters() {
        return count(xpath("//select[contains(@name,'period') or contains(@id,'period')]"
                + " | //input[contains(@name,'date') or contains(@id,'date')]"
                + " | //a[contains(@href,'/stat/')]")) > 0;
    }
}

