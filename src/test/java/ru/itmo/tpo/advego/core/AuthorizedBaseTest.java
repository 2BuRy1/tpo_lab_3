package ru.itmo.tpo.advego.core;

import org.junit.jupiter.api.BeforeEach;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.config.TestCredentials;
import ru.itmo.tpo.advego.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class AuthorizedBaseTest extends BaseTest {
//    @BeforeEach
//    protected void authorize() {
//        driver.get(TestConfig.baseUrl() + "/notices/");
//        LoginPage page = new LoginPage(driver, TestConfig.timeout(), TestConfig.baseUrl());
//        if (page.hasAuthorizedHeader()) {
//            return;
//        }
//
//        page.open();
//        if (TestConfig.manualLogin()) {
//            page.waitForAuthorizedHeader(TestConfig.manualLoginTimeout());
//            return;
//        }
//
//        if (!TestCredentials.isConfigured()) {
//            throw new IllegalStateException(
//                    "Для авторизованных тестов нужна либо живая сохраненная сессия, либо manualLogin=true, либо переменные ADVEGO_LOGIN и ADVEGO_PASSWORD."
//            );
//        }
//
//        page.loginAs(TestCredentials.login(), TestCredentials.password());
//        page.waitForAuthorizedHeader();
//
//        assertFalse(
//                page.isStillOnLoginForm() && page.hasLoginError(),
//                "Авторизация должна завершаться успешно при валидных учетных данных."
//        );
//    }
}
