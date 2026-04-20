package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.AuthorizedBaseTest;
import ru.itmo.tpo.advego.pages.ExecutorStatsPage;
import ru.itmo.tpo.advego.pages.MoneyOutPage;
import ru.itmo.tpo.advego.pages.NoticesPage;
import ru.itmo.tpo.advego.pages.ProfilePage;

import static org.junit.jupiter.api.Assertions.*;

class AccountAreaTest extends AuthorizedBaseTest {
    @Test
    void shouldShowSecurityRelevantLoginNotices() throws InterruptedException {
        NoticesPage page = new NoticesPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();


        assertAll(
                () -> assertTrue(page.hasNoticeTable(), "Список извещений должен отображаться в таблице."),
                () -> assertTrue(page.noticesCount() > 0, "В извещениях должна присутствовать хотя бы одна реальная запись."),
                () -> assertTrue(page.hasNonEmptyNoticeMessage(), "Хотя бы одно извещение должно содержать непустой текст сообщения.")
        );
    }

    @Test
    void shouldExposeProfileEditingConstraintsForNewExecutor() {
        ProfilePage page = new ProfilePage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertAll(
                () -> assertTrue(page.hasAboutAndEducationRestriction(), "Для аккаунта должно отображаться ограничение на редактирование разделов \"О себе\" и \"Образование\"."),
                () -> assertTrue(page.isAboutFieldDisabled(), "Поле \"О себе\" должно быть недоступно до выполнения 50 работ."),
                () -> assertTrue(page.isEducationFieldDisabled(), "Поле \"Образование\" должно быть недоступно до выполнения 50 работ."),
                () -> assertTrue(page.hasSaveButton(), "Кнопка сохранения профиля должна быть доступна.")
        );
    }

    @Test
    void shouldShowWithdrawalToCardControls() {
        MoneyOutPage page = new MoneyOutPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertAll(
                () -> assertTrue(page.hasHeading(), "На странице вывода средств должен отображаться заголовок раздела."),
                () -> assertTrue(page.hasCardWithdrawalOption(), "На странице должна быть доступна опция вывода средств на карту."),
                () -> assertTrue(page.hasAmountField(), "Форма вывода средств должна содержать поле суммы."),
                () -> assertTrue(page.hasWithdrawSubmitAction(), "На форме должна быть доступна кнопка/действие отправки заявки на вывод.")
        );
    }

    @Test
    void shouldOpenExecutorStatisticsPageWithMainBlocks() {
        ExecutorStatsPage page = new ExecutorStatsPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();

        assertAll(
                () -> assertTrue(page.hasHeading(), "В разделе статистики должен отображаться заголовок."),
                () -> assertTrue(page.hasStatsTableOrBlocks(), "На странице статистики должны присутствовать таблица или блоки статистики."),
                () -> assertTrue(page.hasPeriodOrDateFilters(), "В статистике должны быть доступны фильтры по периоду или дате.")
        );
    }
}
