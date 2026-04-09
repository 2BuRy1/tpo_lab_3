package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketingToolsTest extends BaseTest {
    @Test
    void shouldGeneratePasswordThatMatchesSelectedPolicy() {
        PasswordGeneratorPage page = new PasswordGeneratorPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.setDigits(false);
        page.setLowercase(true);
        page.setUppercase(false);
        page.setSymbols(false);
        page.setLength("10");
        page.generate();

        String password = page.generatedPassword();
        assertEquals(10, password.length());
        assertTrue(password.matches("[a-z]{10}"), "Пароль должен содержать только 10 строчных латинских букв.");
    }

    @Test
    void shouldCalculateCtrAsPercentage() {
        CtrCalculatorPage page = new CtrCalculatorPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.enterShows("400");
        page.enterClicks("50");
        page.calculate();

        assertEquals("12.5%", page.result());
    }

    @Test
    void shouldCalculateLtvCorrectly() {
        LtvCalculatorPage page = new LtvCalculatorPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open()
                .setAov("100")
                .setRpr("2")
                .setLifetime("6")
                .clickCalculate();

        assertTrue(page.isResultEqualTo("1200%"));
    }

    @Test
    void shouldCalculateRoiCorrectly() {
        RoiCalculatorPage page = new RoiCalculatorPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open()
                .setProfit("1500")
                .setSpending("1000")
                .clickCalculate();

        assertTrue(page.isResultEqualTo("50%"));
    }

    @Test
    void shouldCalculateRomiCorrectly() {
        RomiCalculatorPage page = new RomiCalculatorPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open()
                .setProfit("1500")
                .setSpending("1000")
                .clickCalculate();

        assertTrue(page.isResultEqualTo("50%"));
    }


}
