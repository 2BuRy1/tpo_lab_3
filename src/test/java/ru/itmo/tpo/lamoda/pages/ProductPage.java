package ru.itmo.tpo.lamoda.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.lamoda.core.BasePage;

public class ProductPage extends BasePage {
    private static final String PRODUCT_PATH = "/p/rtladz220701/clothes-adidasoriginals-futbolka/";

    private static final By[] PRODUCT_TITLE = new By[] {
            By.xpath("//h1"),
            By.xpath("//main//*[self::h1 or self::h2][1]")
    };

    private static final By[] DESCRIPTION_TABS = new By[] {
            By.xpath("//*[contains(.,'О товаре')]"),
            By.xpath("//*[contains(.,'Отзывы')]"),
            By.xpath("//*[contains(.,'Вопросы')]")
    };

    private static final By[] PURCHASE_WIDGET = new By[] {
            By.xpath("//button[contains(.,'В корзину')]"),
            By.xpath("//button[contains(.,'Добавить')]"),
            By.xpath("//*[contains(.,'Товара нет в наличии')]")
    };

    public ProductPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public ProductPage openReferenceProduct() {
        openRelative(PRODUCT_PATH);
        return this;
    }

    public boolean isLoaded() {
        return isVisible(PRODUCT_TITLE);
    }

    public boolean hasDescriptionSections() {
        return isVisible(DESCRIPTION_TABS);
    }

    public boolean hasPurchaseWidget() {
        return isVisible(PURCHASE_WIDGET);
    }
}

