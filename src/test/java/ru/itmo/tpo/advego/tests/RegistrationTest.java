package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.RegistrationPage;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationTest extends BaseTest {
    @Test
    void shouldRunRegistrationFlowAndReceiveSystemResponse() {
        RegistrationPage page = new RegistrationPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();
        String email = "autotest+" + Instant.now().toEpochMilli() + "@example.com";
        String nickname = "autotest" + Instant.now().toEpochMilli();

        assertTrue(page.hasEmailInput(), "В форме регистрации должно быть поле e-mail/логина.");

        page.registerAs(email, nickname);
        page.waitForRegistrationAttemptResult();

        assertTrue(
                page.hasRegistrationSuccessMessage()
                        || page.hasRegistrationErrorMessage()
                        || page.hasCaptchaChallenge()
                        || page.isAuthorized(),
                "После отправки формы должен появиться результат: успех, ошибка валидации, капча или авторизованная сессия."
        );
    }
}
