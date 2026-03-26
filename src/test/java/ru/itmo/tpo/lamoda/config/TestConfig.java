package ru.itmo.tpo.lamoda.config;

import java.time.Duration;

public final class TestConfig {
    private static final String DEFAULT_BASE_URL = "https://www.lamoda.ru";
    private static final long DEFAULT_TIMEOUT_SECONDS = 20L;
    private static final String DEFAULT_USER_AGENT = """
            Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36
            """
;
    private TestConfig() {
    }

    public static BrowserType browser() {
        return BrowserType.from(System.getProperty("browser", "firefox"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", "false"));
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", DEFAULT_BASE_URL);
    }

    public static Duration timeout() {
        long seconds = Long.parseLong(System.getProperty("timeoutSeconds", String.valueOf(DEFAULT_TIMEOUT_SECONDS)));
        return Duration.ofSeconds(seconds);
    }

    public static String userAgent() {
        return System.getProperty("userAgent", DEFAULT_USER_AGENT);
    }
}
