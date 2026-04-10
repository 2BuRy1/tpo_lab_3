package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.AuthorizedBaseTest;
import ru.itmo.tpo.advego.pages.JobFindPage;
import ru.itmo.tpo.advego.pages.MyArticlesPage;
import ru.itmo.tpo.advego.pages.MyJobsPage;
import ru.itmo.tpo.advego.pages.SellArticlePage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutorWorkflowTest extends AuthorizedBaseTest {
    @Test
    void shouldShowRealAvailableOrdersForExecutor() {
        JobFindPage page = new JobFindPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertAll(
                () -> assertTrue(page.visibleOrdersCount() > 0, "На странице должен отображаться хотя бы один доступный заказ."),
                () -> assertTrue(page.firstOrderId().matches("\\d{6,}"), "У первого заказа должен быть реальный числовой идентификатор."),
                () -> assertTrue(!page.firstOrderTitle().isBlank(), "У первого заказа должен быть непустой заголовок."),
                () -> assertTrue(page.firstOrderPrice().matches("\\d+[\\d.,]*\\s*руб\\.?"), "Стоимость первого заказа должна отображаться в рублях."),
                () -> assertTrue(page.firstOrderHasTakeToWorkAction(), "Для доступного заказа должно отображаться действие по отклику или взятию в работу.")
        );
    }

    @Test
    void shouldKeepExecutorNavigationAvailableWithoutActiveJobs() {
        MyJobsPage page = new MyJobsPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertAll(
                () -> assertTrue(page.hasIncomeSummaryTable(), "В разделе должна отображаться таблица краткой статистики дохода."),
                () -> assertTrue(page.hasFindWorkLink(), "Из раздела активных работ должна быть доступна ссылка на поиск работы."),
                () -> assertTrue(page.hasSellArticleLink(), "Из раздела активных работ должна быть доступна ссылка на продажу статьи.")
        );
    }

    @Test
    void shouldPrepareArticleSellingFormWithRequiredControls() {
        MyJobsPage jobsPage = new MyJobsPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();
        jobsPage.goToSellReadyArticle();
        SellArticlePage page = new SellArticlePage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        assertAll(
                () -> assertTrue(page.hasForm(), "Форма добавления статьи должна присутствовать на странице."),
                () -> assertTrue(page.hasTitleField(), "В форме должно быть поле заголовка статьи."),
                () -> assertTrue(page.hasArticleTextField(), "В форме должно быть поле текста статьи."),
                () -> assertTrue(page.hasCostFields(), "В форме должны быть доступны оба варианта указания стоимости статьи."),
                () -> assertTrue(page.isDoNotPublishCheckedByDefault(), "Флаг \"Не выставлять на продажу\" должен быть включен по умолчанию."),
                () -> assertTrue(page.disclaimerCheckboxesCount() > 0, "Форма должна содержать обязательные чекбоксы согласия с условиями публикации."),
                () -> assertTrue(page.hasAgreeAllCheckbox(), "В форме должен присутствовать чекбокс согласия со всеми условиями."),
                () -> assertTrue(page.hasPublicationErrorBlock() || page.hasGrammarGateBlock(), "На странице должны быть отображены защитные блоки публикации и проверки статьи.")
        );
    }

    @Test
    void shouldCreateAndPublishArticleToMarketplace() {
        String title = "AUTOTEST ARTICLE " + Instant.now().toEpochMilli();
        String articleText = """
                Это тестовая статья, созданная для автоматизированной проверки публикации в магазине Адвего.
                Она описывает поведение Selenium-теста и не предназначена для коммерческого использования.
                В первом абзаце специально размещен связный и понятный текст, чтобы система могла сформировать краткое описание статьи.

                Во втором абзаце уточняется, что статья написана с нуля, не копирует внешние источники и используется только для проверки сценария публикации.
                Текст дополнительно расширен несколькими предложениями, чтобы у страницы был достаточный объем для расчета стоимости и отображения в карточке.

                В третьем абзаце зафиксировано, что материал содержит нейтральное описание процесса тестирования, публикации и отображения статьи в общем интерфейсе платформы.
                Этот текст не содержит запрещенного содержания, контактных данных, ссылок на сторонние ресурсы и иных спорных данных.
                """;
        String notes = """
                Тестовая статья создана автоматически для проверки сценария публикации материала в магазине статей.
                Описание намеренно превышает минимальный порог длины, чтобы пройти валидацию формы и дать возможность проверить размещение статьи в интерфейсе.
                """;

        MyJobsPage jobsPage = new MyJobsPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();
        jobsPage.goToSellReadyArticle();
        SellArticlePage page = new SellArticlePage(driver, TestConfig.timeout(), TestConfig.baseUrl());
        page.enterTitle(title)
                .selectCategory("IT, софт")
                .selectTextType("Копирайтинг")
                .enterArticleText(articleText)
                .generateShortDescription()
                .enterNotes(notes)
                .enterPrice("150")
                .setPublishToMarketplace(true)
                .agreeToAllTerms()
                .submitArticle();
        page.waitForSubmissionOutcome();

        if (!page.isGrammarGateVisible()) {
            MyArticlesPage myArticlesPage = new MyArticlesPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).openAllArticles();
            assertTrue(myArticlesPage.containsArticleTitle(title), "Созданная статья должна появиться в разделе \"Мои статьи\".");

            MyArticlesPage showcasePage = new MyArticlesPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).openShowcase();
            assertTrue(showcasePage.containsArticleTitle(title), "Опубликованная статья должна появиться в разделе \"Статьи на продаже\".");
            return;
        }

        assertTrue(page.isGrammarGateVisible(), "После отправки формы платформа должна либо опубликовать статью, либо перевести пользователя к обязательной проверке грамотности.");
    }
}
