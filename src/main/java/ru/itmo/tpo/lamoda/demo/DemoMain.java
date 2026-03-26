package ru.itmo.tpo.lamoda.demo;

import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class DemoMain {
    private DemoMain() {
    }

    public static void main(String[] args) throws InterruptedException {
        String browser = System.getProperty("browser", "chrome").trim().toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        long pauseSeconds = Long.parseLong(System.getProperty("pauseSeconds", "5"));

        WebDriver driver = createDriver(browser, headless);
        try {
            URL pageUrl = Objects.requireNonNull(
                    DemoMain.class.getClassLoader().getResource("demo/demo-page.html"),
                    "Demo page not found on the classpath."
            );

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1200, 800));
            driver.get(pageUrl.toExternalForm());

            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h1[contains(.,'Тестовая страница Selenium')]")
            ));
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@data-test='demo-button']")
            ));

            button.click();

            WebElement status = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='status' and contains(.,'Кнопка нажата')]")
            ));

            System.out.println("DEMO PASS");
            System.out.println("Browser: " + browser);
            System.out.println("Headless: " + headless);
            System.out.println("Title: " + title.getText());
            System.out.println("Status: " + status.getText());

            if (pauseSeconds > 0) {
                Thread.sleep(Duration.ofSeconds(pauseSeconds).toMillis());
            }
        } finally {
            driver.quit();
        }
    }

    private static WebDriver createDriver(String browser, boolean headless) {
        return switch (browser) {
            case "firefox", "ff" -> new FirefoxDriver(buildFirefoxOptions(headless));
            default -> new ChromeDriver(buildChromeOptions(headless));
        };
    }

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1200,800");
        options.addArguments("--disable-notifications");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1200");
        options.addArguments("--height=800");
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }
}
