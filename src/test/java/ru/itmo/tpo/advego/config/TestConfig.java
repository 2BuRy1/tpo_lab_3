package ru.itmo.tpo.advego.config;

import java.time.Duration;
import java.nio.file.Path;

public final class TestConfig {
    private static final String DEFAULT_BASE_URL = "https://advego.com";
    private static final long DEFAULT_TIMEOUT_SECONDS = 45L;
    private static final String DEFAULT_USER_AGENT = """
            Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36
            """;

    private TestConfig() {
    }

    public static BrowserType browser() {
        return BrowserType.from(System.getProperty("browser", "chrome"));
    }

    public static boolean headless() {
        return false;
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

    public static boolean reuseSession() {
        return Boolean.parseBoolean(System.getProperty("reuseSession", "true"));
    }

    public static boolean manualLogin() {
        return Boolean.parseBoolean(System.getProperty("manualLogin", "false"));
    }

    public static Duration manualLoginTimeout() {
        long seconds = Long.parseLong(System.getProperty("manualLoginTimeoutSeconds", "180"));
        return Duration.ofSeconds(seconds);
    }

    public static Duration authPasswordDelay() {
        long seconds = Long.parseLong(System.getProperty("authPasswordDelaySeconds", "3"));
        return Duration.ofSeconds(seconds);
    }

    public static Path browserProfileDir(BrowserType browserType) {
        String configured = System.getProperty("browserProfileDir");
        if (configured != null && !configured.isBlank()) {
            return Path.of(configured).toAbsolutePath();
        }
        return Path.of(".selenium-profile", browserType.name().toLowerCase()).toAbsolutePath();
    }
}
