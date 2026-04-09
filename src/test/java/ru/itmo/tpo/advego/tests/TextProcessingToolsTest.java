package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.FindWordsPage;
import ru.itmo.tpo.advego.pages.KeyboardCorrectingPage;
import ru.itmo.tpo.advego.pages.RegisterConverterPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextProcessingToolsTest extends BaseTest {
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
}
