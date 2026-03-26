package ru.itmo.tpo.lamoda.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lamoda.config.TestConfig;
import ru.itmo.tpo.lamoda.core.BaseTest;
import ru.itmo.tpo.lamoda.pages.CartPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CartPageTest extends BaseTest {
    @Test
    void shouldOpenCartPage() {
        CartPage cartPage = new CartPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertTrue(cartPage.isLoaded(), "Страница корзины не загрузилась.");
    }
}
