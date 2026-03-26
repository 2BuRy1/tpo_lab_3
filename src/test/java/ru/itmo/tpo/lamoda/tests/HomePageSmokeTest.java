package ru.itmo.tpo.lamoda.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lamoda.config.TestConfig;
import ru.itmo.tpo.lamoda.core.BaseTest;
import ru.itmo.tpo.lamoda.pages.HomePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HomePageSmokeTest extends BaseTest {
    @Test
    void shouldOpenHomePageAndShowMainNavigation() {
        HomePage homePage = new HomePage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertTrue(homePage.isLoaded(), "Главная страница Lamoda не загрузилась.");
        assertTrue(homePage.hasMainNavigation(), "На главной странице не найдена основная навигация.");
    }
}

