package ru.itmo.tpo.advego.tests;

import org.junit.jupiter.api.Test;
import ru.itmo.tpo.advego.config.TestConfig;
import ru.itmo.tpo.advego.core.BaseTest;
import ru.itmo.tpo.advego.pages.IpInfoPage;
import ru.itmo.tpo.advego.pages.SiteIksPage;
import ru.itmo.tpo.advego.pages.UrlConverterPage;
import ru.itmo.tpo.advego.pages.WhoisPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SeoProcessingToolsTest extends BaseTest {


    @Test
    void shouldCheckIksForVkCom() {
        SiteIksPage page = new SiteIksPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open();
        assertTrue(page.isPageOpened());

        page.enterDomain("vk.com");
        page.clickCheck();

        page.waitForIksResult(Duration.ofSeconds(30));

        assertEquals("vk.com", page.getDomainValue());
        assertTrue(page.isResultShown());
        assertFalse(page.getIksValue().isBlank());
    }

    @Test
    void shouldShowIpInfo() {
        IpInfoPage page = new IpInfoPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open();

        assertTrue(page.isOpened());
        assertTrue(page.hasNonEmptyIp());
        assertTrue(page.hasNonEmptyLocation() || page.hasNonEmptyProviderAddress(),
                "На странице IP-инфо должна присутствовать хотя бы одна непустая дополнительная характеристика (локация или адрес провайдера).");
    }

    @Test
    void shouldCheckWhoisForVkCom() {
        WhoisPage page = new WhoisPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open()
                .typeDomain("vk.com")
                .submit();

        assertTrue(page.hasWhoisResult());
        assertTrue(page.containsDomain("vk.com"));
    }

    @Test
    void shouldConvertRussianTextToSeoUrl() {
        UrlConverterPage page = new UrlConverterPage(driver, TestConfig.timeout(), TestConfig.baseUrl());

        page.open()
                .typeText("зима в деревне");

        assertTrue(page.outputIsNotEmpty());
        assertTrue(page.isConvertedTo("zima-v-derevne"));
    }
}

