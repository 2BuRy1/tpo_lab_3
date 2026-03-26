package ru.itmo.tpo.lamoda.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lamoda.config.TestConfig;
import ru.itmo.tpo.lamoda.core.BaseTest;
import ru.itmo.tpo.lamoda.pages.ProductPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductPageTest extends BaseTest {
    @Test
    void shouldOpenProductCardAndShowDescriptionBlocks() {
        ProductPage productPage = new ProductPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).openReferenceProduct();

        assertTrue(productPage.isLoaded(), "Карточка товара не открылась.");
        assertTrue(productPage.hasDescriptionSections(), "На карточке товара не найдены информационные секции.");
        assertTrue(productPage.hasPurchaseWidget(), "На карточке товара не найден блок покупки.");
    }
}

