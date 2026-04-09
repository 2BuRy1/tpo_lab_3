package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextProcessingToolsTest extends BaseTest {

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

    @Test
    void shouldConvertTextRegisterAcrossSupportedModes() {
        RegisterConverterPage page = new RegisterConverterPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.enterText("ПрИвЕт мир. вТОРОЕ предложение");
        page.toLowerCase();
        assertEquals("привет мир. второе предложение", page.convertedText());

        page.toUpperCase();
        assertEquals("ПРИВЕТ МИР. ВТОРОЕ ПРЕДЛОЖЕНИЕ", page.convertedText());

        page.enterText("пРИВЕТ мир. вТОРОЕ предложение");
        page.firstUpper();
        assertEquals("Привет мир. Второе предложение", page.convertedText());

        page.enterText("пРИВЕТ миР");
        page.allWordsFirstUpper();
        assertEquals("Привет Мир", page.convertedText());
    }

    @Test
    void shouldFixKeyboardLayoutInBothDirections() {
        KeyboardCorrectingPage page = new KeyboardCorrectingPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.enterText("ghbdtn vbh");
        page.convertToRussian();
        assertEquals("привет мир", page.textValue());

        page.enterText("руддщ");
        page.convertToLatin();
        assertEquals("hello", page.textValue());
    }

    @Test
    void shouldFindExactWordMatchesInText() {
        FindWordsPage page = new FindWordsPage(driver, TestConfig.timeout(), TestConfig.baseUrl())
                .open()
                .findMatches("кот и пес. кот и кот.", "кот");

        assertAll(
                () -> assertEquals("3", page.matchesCount()),
                () -> assertTrue(page.hasResultRow("кот")),
                () -> assertEquals(3, page.highlightedMatches())
        );
    }

    @Test
    void shouldStartAntiPlagiatProcess() {
        AntiplagiatPage page = new AntiplagiatPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.open()
                .enterText("Какой-то текст для проверки уникальности")
                .waitAndSubmit(Duration.ofSeconds(30))
                .waitForQueuedMessage();

        assertTrue(page.isQueuedMessageDisplayed());
    }

    @Test
    void shouldCountCharsWordsAndCharsWithoutSpaces() {
        String input = "Привет мир";


        CharCounterPage page = new CharCounterPage(driver, TestConfig.timeout(), TestConfig.baseUrl())
                .open()
                .enterText(input)
                .waitForCountsToBeUpdated();

        assertEquals("10", page.getSymbolsCount());
        assertEquals("9", page.getSymbolsNoSpaceCount());
        assertEquals("2", page.getWordsCount());
    }

    @Test
    void shouldShowValidationErrorWhenTextIsEmpty() {
        SpellCheckPage page = new SpellCheckPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.clickCheckWithoutText();

        assertTrue(page.isEmptyTextErrorVisible(), "Пустой текст должен блокировать запуск проверки.");
    }

    @Test
    void shouldTransliterateRussianText() {
        TransliterationPage page = new TransliterationPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open();
        assertTrue(page.isPageOpened());

        page.enterText("привет");
        assertEquals("privet", page.getResultText());
    }

    @Test
    void shouldFlipText() {
        FlipTextPage page = new FlipTextPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open();
        assertTrue(page.isPageOpened());

        page.enterText("привет");
        page.clickFlip();

        assertEquals("ɯǝʚиdu", page.getResultText());
    }

    @Test
    void shouldReverseText() {
        ReverseTextPage page = new ReverseTextPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open();
        assertTrue(page.isPageOpened());

        page.enterText("привет");
        page.clickReverse();

        assertEquals("тевирп", page.getText());
    }

    @Test
    void shouldReplaceOccurrencesInText() {
        ReplaceTextPage page = new ReplaceTextPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open();
        assertTrue(page.isPageOpened());

        page.enterSearchText("кот");
        page.enterReplaceText("пёс");
        page.enterSourceText("кот спит, кот ест");

        page.clickReplace();

        assertEquals("пёс спит, пёс ест", page.getResultText());
    }
}
