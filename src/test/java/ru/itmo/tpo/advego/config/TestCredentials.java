package ru.itmo.tpo.advego.config;

public final class TestCredentials {
    private TestCredentials() {
    }

    public static String login() {
       //return readRequired("ADVEGO_LOGIN");
        return "kot514877@gmail.com";
    }

    public static String password() {
        //return readRequired("ADVEGO_PASSWORD");
        return "Kirill1ghjff";
    }

    public static boolean isConfigured() {
        return isPresent("ADVEGO_LOGIN") && isPresent("ADVEGO_PASSWORD");
    }

    private static String readRequired(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Environment variable " + name + " must be provided for authorized tests.");
        }
        return value;
    }

    private static boolean isPresent(String name) {
        String value = System.getenv(name);
        return value != null && !value.isBlank();
    }
}
