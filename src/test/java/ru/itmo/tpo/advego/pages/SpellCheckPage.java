package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class SpellCheckPage extends BasePage {
    public SpellCheckPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public SpellCheckPage open() {
        openRelative("/text/");
        waitForVisible(xpath("//form[contains(@action,'/text/') and contains(@class,'action_form')]//textarea[@name='text_text']"));
        return this;
    }

    public void clickCheckWithoutText() {
        click(xpath("//div[contains(@class,'botmenu')]//a[normalize-space()='Проверить']"));
    }

    public boolean isEmptyTextErrorVisible() {
        return isVisible(xpath("//div[contains(@class,'error') and contains(normalize-space(),'Напишите текст для проверки')]"));
    }

    public boolean isResultsPanelVisible() {
        return isVisible(xpath("//div[contains(@class,'list_item') and .//h3[normalize-space()='Результаты проверки']]"));
    }
}
