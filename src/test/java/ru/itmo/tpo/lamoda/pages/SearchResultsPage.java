package ru.itmo.tpo.lamoda.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.lamoda.core.BasePage;

public class SearchResultsPage extends BasePage {
    private static final By[] PRODUCT_CARDS = new By[] {
            By.xpath("//a[contains(@href,'/p/')]"),
            By.xpath("//article//a[contains(@href,'/p/')]")
    };

    private static final By[] RESULTS_MARKERS = new By[] {
            By.xpath("//*[contains(.,'Найдено')]"),
            By.xpath("//*[contains(.,'результат')]"),
            By.xpath("(//a[contains(@href,'/p/')])[1]")
    };

    public SearchResultsPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public boolean isLoaded() {
        return isVisible(RESULTS_MARKERS);
    }

    public int productCount() {
        return countVisible(PRODUCT_CARDS);
    }
}

