package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.SpellCheckPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SpellCheckPageTest extends BaseTest {
    @Test
    void shouldShowValidationErrorWhenTextIsEmpty() {
        SpellCheckPage page = new SpellCheckPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.clickCheckWithoutText();

        assertTrue(page.isEmptyTextErrorVisible(), "Пустой текст должен блокировать запуск проверки.");
    }
}
