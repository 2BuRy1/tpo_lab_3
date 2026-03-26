package ru.itmo.tpo.lamoda.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.lamoda.core.BasePage;

public class CategoryPage extends BasePage {
    private static final String WOMEN_SHOES_PATH = "/c/15/shoes-women/";

    private static final By[] CATEGORY_MARKERS = new By[] {
            By.xpath("//main"),
            By.xpath("//h1"),
            By.xpath("//nav//*[self::a or self::span][contains(.,'Обув') or contains(.,'Жен')]"),
            By.xpath("//*[contains(.,'Фильтр') or contains(.,'Сортировать')]"),
            By.xpath("//*[contains(.,'товар') or contains(.,'модел')]")
    };

    private static final By[] PRODUCT_CARDS = new By[] {
            By.xpath("//a[contains(@href,'/p/')]"),
            By.xpath("//article//a[contains(@href,'/p/')]"),
            By.xpath("//a[contains(@href,'/clothes-') or contains(@href,'/shoes-')]")
    };

    private static final By[] FILTER_CONTROLS = new By[] {
            By.xpath("//button[contains(.,'Фильтр')]"),
            By.xpath("//button[contains(.,'Сортировать')]"),
            By.xpath("//*[contains(.,'Фильтр') or contains(.,'Сортировать')]"),
            By.xpath("//label[contains(.,'Размер') or contains(.,'Бренд') or contains(.,'Цена')]")
    };

    public CategoryPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public CategoryPage openWomenShoes() {
        openRelative(WOMEN_SHOES_PATH);
        return this;
    }

    public boolean isLoaded() {
        if (isAntiBotPage()) {
            throw new AssertionError("Lamoda returned a Qrator anti-bot page instead of the category page.");
        }
        return hasCategoryLikeUrl() && isVisible(CATEGORY_MARKERS);
    }

    public boolean hasFilterControls() {
        return isVisible(FILTER_CONTROLS);
    }

    public int productCount() {
        waitForVisible(CATEGORY_MARKERS);
        return countVisible(PRODUCT_CARDS);
    }

    private boolean hasCategoryLikeUrl() {
        String currentUrl = currentUrl().toLowerCase();
        return currentUrl.contains("/c/")
                || currentUrl.contains("shoes-women")
                || currentUrl.contains("catalog");
    }
}
