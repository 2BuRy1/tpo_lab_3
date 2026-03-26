package ru.itmo.tpo.lamoda.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.lamoda.core.BasePage;

public class CartPage extends BasePage {
    private static final By[] CART_MARKERS = new By[] {
            By.xpath("//*[contains(translate(normalize-space(.), 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ', 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'), 'корзин')]"),
            By.xpath("//*[contains(translate(normalize-space(.), 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ', 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'), 'пуст')]"),
            By.xpath("//a[contains(@href,'/catalog/')]"),
            By.xpath("//button[contains(.,'Перейти') or contains(.,'Выбрать')]")
    };

    public CartPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public CartPage open() {
        openRelative("/cart/");
        return this;
    }

    public boolean isLoaded() {
        if (isAntiBotPage()) {
            throw new AssertionError("Lamoda returned a Qrator anti-bot page instead of the cart page.");
        }
        return hasCartLikeUrl() && isVisible(CART_MARKERS);
    }

    private boolean hasCartLikeUrl() {
        String currentUrl = currentUrl().toLowerCase();
        return currentUrl.contains("/cart")
                || currentUrl.contains("/basket")
                || currentUrl.contains("korzina");
    }
}
