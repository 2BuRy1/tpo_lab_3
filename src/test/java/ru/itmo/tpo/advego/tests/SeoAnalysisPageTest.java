package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.SeoAnalysisPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeoAnalysisPageTest extends BaseTest {
    @Test
    void shouldCalculateDeterministicMetricsForSimpleRussianText() {
        SeoAnalysisPage page = new SeoAnalysisPage(driver, TestConfig.timeout(), TestConfig.baseUrl())
                .open()
                .analyze("мама мыла раму.");

        assertAll(
                () -> assertEquals("16", page.statValue("Количество символов")),
                () -> assertEquals("13", page.statValue("Количество символов без пробелов")),
                () -> assertEquals("3", page.statValue("Количество слов")),
                () -> assertEquals("3", page.statValue("Количество уникальных слов")),
                () -> assertEquals("2", page.statValue("Количество значимых слов")),
                () -> assertEquals("0", page.statValue("Количество стоп-слов")),
                () -> assertEquals("33.3 %", page.statValue("Вода")),
                () -> assertEquals("0", page.statValue("Количество грамматических ошибок")),
                () -> assertEquals("1.00", page.statValue("Классическая тошнота документа")),
                () -> assertEquals("0.0 %", page.statValue("Академическая тошнота документа")),
                () -> assertTrue(page.languageSummary().contains("Russian - Русский")),
                () -> assertTrue(page.languageSummary().contains("Ошибок не найдено")),
                () -> assertEquals("1", page.wordCount("мама")),
                () -> assertEquals("33.33", page.wordFrequency("мама")),
                () -> assertEquals("1", page.wordCount("мыть")),
                () -> assertEquals("1", page.wordCount("рам")),
                () -> assertEquals("мама мыла раму.", page.analyzedText())
        );
    }
}
