package ru.itmo.tpo.lamoda.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.lamoda.core.BasePage;

public class HomePage extends BasePage {
    private static final By[] SEARCH_INPUTS = new By[] {
            By.xpath("//input[contains(@placeholder,'Поиск')]"),
            By.xpath("//input[contains(@aria-label,'Поиск')]"),
            By.xpath("//input[contains(@name,'search')]")
    };

    private static final By[] MAIN_NAVIGATION = new By[] {
            By.xpath("//a[contains(.,'Женщин')]"),
            By.xpath("//a[contains(.,'Мужчин')]"),
            By.xpath("//a[contains(.,'Дет')]")
    };

    public HomePage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public HomePage open() {
        openRelative("/");
        return this;
    }

    public boolean isLoaded() {
        return isVisible(SEARCH_INPUTS);
    }

    public boolean hasMainNavigation() {
        return isVisible(MAIN_NAVIGATION);
    }

    public SearchResultsPage searchFor(String query) {
        typeAndSubmit(query, SEARCH_INPUTS);
        return new SearchResultsPage(driver, timeout(), baseUrl());
    }
}
