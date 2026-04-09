package ru.itmo.tpo.advego.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

import java.time.Duration;

public class CharCounterPage extends BasePage {

    private final By textArea = xpath("//textarea[@id='textcheck' and not(@tabindex='-9999')]");
    private final By symbolsCount = By.id("symbolscount");
    private final By symbolsNoSpaceCount = By.id("symbolscount_no_space");
    private final By wordsCount = By.id("wordscount");

    public CharCounterPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public CharCounterPage open() {
        openRelative("/info/znakoschitalka-simvolov-online/");
        waitForVisible(textArea);
        return this;
    }

    public CharCounterPage enterText(String text) {
        type(textArea, text);
        return this;
    }

    public CharCounterPage waitForCountsToBeUpdated() {
        wait.until(driver -> {
            String symbols = text(symbolsCount).replace(" ", "");
            String noSpace = text(symbolsNoSpaceCount).replace(" ", "");
            String words = text(wordsCount).replace(" ", "");

            return !symbols.equals("0") || !noSpace.equals("0") || !words.equals("0");
        });
        return this;
    }

    public String getSymbolsCount() {
        return text(symbolsCount).replace(" ", "");
    }

    public String getSymbolsNoSpaceCount() {
        return text(symbolsNoSpaceCount).replace(" ", "");
    }

    public String getWordsCount() {
        return text(wordsCount).replace(" ", "");
    }
}