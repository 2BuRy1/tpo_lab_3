package ru.itmo.tpo.lamoda.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lamoda.config.TestConfig;
import ru.itmo.tpo.lamoda.core.BaseTest;
import ru.itmo.tpo.lamoda.pages.HomePage;
import ru.itmo.tpo.lamoda.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchFlowTest extends BaseTest {
    @Test
    void shouldFindProductsBySearchQuery() {
        HomePage homePage = new HomePage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();
        SearchResultsPage resultsPage = homePage.searchFor("кеды");

        assertTrue(resultsPage.isLoaded(), "Страница поисковой выдачи не открылась.");
        assertTrue(resultsPage.productCount() > 0, "По запросу не найдено ни одной карточки товара.");
    }
}

