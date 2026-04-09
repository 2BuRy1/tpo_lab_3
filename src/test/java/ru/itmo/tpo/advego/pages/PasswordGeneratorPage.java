package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class PasswordGeneratorPage extends BasePage {
    public PasswordGeneratorPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public PasswordGeneratorPage open() {
        openRelative("/info/generator-parolej-online/");
        waitForVisible(xpath("//h1[contains(normalize-space(),'Генератор паролей')]"));
        return this;
    }

    public void setDigits(boolean checked) {
        setCheckbox(xpath("//label[normalize-space()='Цифры']/preceding-sibling::input[1]"), checked);
    }

    public void setLowercase(boolean checked) {
        setCheckbox(xpath("//label[normalize-space()='Маленькие буквы']/preceding-sibling::input[1]"), checked);
    }

    public void setUppercase(boolean checked) {
        setCheckbox(xpath("//label[normalize-space()='Большие буквы']/preceding-sibling::input[1]"), checked);
    }

    public void setSymbols(boolean checked) {
        setCheckbox(xpath("//label[normalize-space()='Спец. символы']/preceding-sibling::input[1]"), checked);
    }

    public void setLength(String length) {
        type(xpath("//label[normalize-space()='Длина:']/following-sibling::input[1]"), length);
    }

    public void generate() {
        click(xpath("//form[.//button[contains(normalize-space(),'Сгенерировать')]]//button[contains(normalize-space(),'Сгенерировать')]"));
        waitForValue(xpath("//form[.//button[contains(normalize-space(),'Сгенерировать')]]/following-sibling::div[contains(@class,'list')][1]//input[@type='text']"), value -> !value.isBlank());
    }

    public String generatedPassword() {
        return value(xpath("//form[.//button[contains(normalize-space(),'Сгенерировать')]]/following-sibling::div[contains(@class,'list')][1]//input[@type='text']"));
    }
}
