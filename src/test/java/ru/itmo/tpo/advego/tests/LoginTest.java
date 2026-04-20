package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.config.TestCredentials;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.LoginPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest extends BaseTest {
    @Test
    void shouldLoginWithValidCredentials() {
        driver.manage().deleteAllCookies();
        driver.get(TestConfig.baseUrl() + "/logout/");
        LoginPage page = new LoginPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        if (page.hasAuthorizedHeader()) {
            driver.get(TestConfig.baseUrl() + "/logout/");
            page.open();
        }
        page.loginAs(TestCredentials.login(), TestCredentials.password());
        page.waitForLoginAttemptResult(Duration.ofSeconds(20));

        assertFalse(page.isStillOnLoginForm() && page.hasLoginError(), "Логин не должен завершаться ошибкой при валидных учетных данных.");
        assertTrue(page.hasAuthorizedHeader() || page.hasCaptchaChallenge(),
                "После отправки формы ожидается успешная авторизация или показ капчи.");
    }
}
