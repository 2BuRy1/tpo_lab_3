package ru.itmo.tpo.lamoda.tests.qrator;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lamoda.config.TestConfig;
import ru.itmo.tpo.lamoda.core.BaseTest;
import ru.itmo.tpo.lamoda.pages.raw.LamodaRawPage;
import ru.itmo.tpo.lamoda.pages.raw.LamodaRawPage.AccessResult;

class QratorSmokeTest extends BaseTest {
    @Test
    void shouldDetectBlockedAccessOnHomePage() {
        AccessResult accessResult = new LamodaRawPage(driver, TestConfig.timeout(), TestConfig.baseUrl())
                .probePath("/");

        assertNotEquals(AccessResult.OPENED, accessResult,
                "Ожидали, что Lamoda заблокирует автоматизированный доступ, но страница открылась без ограничений.");
    }

    @Test
    void shouldDetectBlockedAccessOnCartPage() {
        AccessResult accessResult = new LamodaRawPage(driver, TestConfig.timeout(), TestConfig.baseUrl())
                .probePath("/cart/");

        assertNotEquals(AccessResult.OPENED, accessResult,
                "Ожидали, что Lamoda заблокирует автоматизированный доступ на корзине, но страница открылась без ограничений.");
    }
}
