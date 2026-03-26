package ru.itmo.tpo.lamoda.config;

public enum BrowserType {
    CHROME,
    FIREFOX;

    public static BrowserType from(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return CHROME;
        }
        return switch (rawValue.trim().toLowerCase()) {
            case "firefox", "ff" -> FIREFOX;
            default -> CHROME;
        };
    }
}

