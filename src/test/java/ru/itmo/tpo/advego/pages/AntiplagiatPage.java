package ru.itmo.tpo.advego.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.itmo.tpo.advego.core.BasePage;

import java.time.Duration;

public class AntiplagiatPage extends BasePage {

    private final By jobTextAreas = xpath("//textarea[@name='job_text']");
    private final By submitButton = xpath("//div[@id='text_check']//a[@rel='unique-check-start']");
    private final By queueMessage = xpath(
            "//*[contains(., 'В очереди на проверку') " +
                    "or contains(., 'Ваш документ будет проверен следующим') " +
                    "or contains(., 'Текст готов к проверке, войдите или зарегистрируйтесь, чтобы запустить проверку и получить ее результат.')]"
    );

    public AntiplagiatPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public AntiplagiatPage open() {
        openRelative("/antiplagiat/");
        waitForVisible(jobTextAreas);
        return this;
    }

    public AntiplagiatPage enterText(String text) {
        WebElement textarea = findVisible(jobTextAreas);
        scrollIntoView(textarea);

        try {
            textarea.clear();
            textarea.sendKeys(text);
        } catch (Exception e) {
            executeScript("arguments[0].value = arguments[1];", textarea, text);
            executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", textarea);
            executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", textarea);
        }

        return this;
    }

    public AntiplagiatPage waitAndSubmit(Duration captchaTime) {
        try {
            Thread.sleep(captchaTime.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ожидание капчи было прервано", e);
        }

        WebElement button = waitForVisible(submitButton);
        scrollIntoView(button);

        try {
            button.click();
        } catch (Exception e) {
            executeScript("arguments[0].click();", button);
        }

        return this;
    }

    public AntiplagiatPage waitForQueuedMessage() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> d.findElements(queueMessage).stream().anyMatch(e -> {
                    try {
                        return e.isDisplayed();
                    } catch (Exception ex) {
                        return false;
                    }
                }));

        return this;
    }

    public boolean isQueuedMessageDisplayed() {
        return driver.findElements(queueMessage).stream().anyMatch(e -> {
            try {
                return e.isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        });
    }
}