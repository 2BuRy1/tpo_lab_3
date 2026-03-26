package ru.itmo.tpo.lamoda.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lamoda.config.TestConfig;
import ru.itmo.tpo.lamoda.core.BaseTest;
import ru.itmo.tpo.lamoda.pages.CategoryPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogPageTest extends BaseTest {
    @Test
    void shouldOpenCategoryPageAndShowFilters() {
        CategoryPage categoryPage = new CategoryPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).openWomenShoes();

        assertTrue(categoryPage.isLoaded(), "Категория не загрузилась.");
        assertTrue(categoryPage.productCount() > 0, "В каталоге не найдено ни одной карточки товара.");
        assertTrue(categoryPage.hasFilterControls(), "На странице категории не найдены элементы фильтрации/сортировки.");
    }
}

