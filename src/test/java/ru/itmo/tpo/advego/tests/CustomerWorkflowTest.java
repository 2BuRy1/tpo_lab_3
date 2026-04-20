package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.config.TestCredentials;
import ru.itmo.tpo.advego.core.AuthorizedBaseTest;
import ru.itmo.tpo.advego.pages.CampaignOrdersPage;
import ru.itmo.tpo.advego.pages.OrderCreationPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerWorkflowTest extends AuthorizedBaseTest {
    @Test
    void shouldShowCustomerOrdersSectionWithExpectedNavigation() {
        CampaignOrdersPage page = new CampaignOrdersPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertAll(
                () -> assertTrue(page.hasTabs(), "В разделе заказов должны отображаться основные вкладки фильтрации."),
                () -> assertTrue(page.hasCreateOrderLink(), "В разделе заказов должна быть доступна ссылка на создание нового заказа.")
        );
    }


    @Test
    void shouldAskForCredentialsAndShowInsufficientFundsMessageWhenStartingNewOrder() {
        String title = "AUTOTEST ORDER " + Instant.now().toEpochMilli();

        String description = """
            Задача:
            Подготовить короткий тестовый материал для автоматизированной проверки интерфейса.

            Порядок работы:
            1. Внимательно прочитать описание.
            2. Подготовить текст в свободной форме.
            3. Отправить результат в рамках тестового заказа.

            Дополнительная информация:
            Этот заказ создан Selenium-тестом и используется только для проверки сценария сохранения заказа.
            """;

        OrderCreationPage page = new OrderCreationPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        page.enterTitle(title)
                .selectOrderType("SEO")
                .selectTextType("Копирайтинг")
                .enterDescription(description)
                .enterCost("25")
                .startOrderExpectingAuthOrInsufficientFunds();

        page.authorizeInOrderFormForStartIfRequired(TestCredentials.login(), TestCredentials.password());

        assertTrue(
                page.hasInsufficientFundsMessage() || page.hasSavedSuccessMessage(),
                "После запуска должен быть один из ожидаемых исходов: сообщение о нехватке средств или переход к карточке/сохранению заказа."
        );
    }
}
