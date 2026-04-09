package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.AuthorizedBaseTest;
import ru.itmo.tpo.advego.pages.NoticesPage;
import ru.itmo.tpo.advego.pages.ProfilePage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountAreaTest extends AuthorizedBaseTest {
    @Test
    void shouldShowSecurityRelevantLoginNotices() throws InterruptedException {
        NoticesPage page = new NoticesPage(driver, TestConfig.timeout(), TestConfig.baseUrl()).open();


        assertAll(
                () -> assertTrue(page.hasNoticeTable(), "Список извещений должен отображаться в таблице."),
                () -> assertTrue(page.noticesCount() > 0, "В извещениях должна присутствовать хотя бы одна реальная запись."),
                () -> assertTrue(page.hasNonEmptyNoticeMessage(), "Хотя бы одно извещение должно содержать непустой текст сообщения."),
                () -> assertTrue(page.hasNoticeMetadata(), "У извещений должна отображаться дата или иная служебная метаинформация.")
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
}
