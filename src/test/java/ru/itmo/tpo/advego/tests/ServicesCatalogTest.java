package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.ServicesPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicesCatalogTest extends BaseTest {
    @Test
    void shouldExposeGuestToolsFromUseCaseDiagram() {
        ServicesPage page = new ServicesPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertAll(
                () -> assertTrue(page.hasHeading("Сервисы Адвего")),
                () -> assertTrue(page.linkHref("Проверка орфографии онлайн").contains("/text/")),
                () -> assertTrue(page.linkHref("SEO-анализ текста онлайн").contains("/text/seo/")),
                () -> assertTrue(page.linkHref("Посчитать количество символов в тексте").contains("/info/znakoschitalka-simvolov-online/")),
                () -> assertTrue(page.linkHref("Поиск слов и фраз в тексте").contains("/info/find_words/")),
                () -> assertTrue(page.linkHref("Исправление текста в неправильной раскладке").contains("/info/keyboard-correcting/")),
                () -> assertTrue(page.linkHref("Конвертер регистров онлайн").contains("/info/register-converter/")),
                () -> assertTrue(page.linkHref("Генератор паролей онлайн").contains("/info/generator-parolej-online/")),
                () -> assertTrue(page.linkHref("Калькулятор CTR показателя").contains("/info/ctr-kalkulyator/"))
        );
    }
}
