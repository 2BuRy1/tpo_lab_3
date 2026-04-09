package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class FindWordsPage extends BasePage {
    public FindWordsPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public FindWordsPage open() {
        openRelative("/info/find_words/");
        waitForVisible(xpath("//h1[contains(normalize-space(),'Поиск слов и фраз')]"));
        return this;
    }

    public FindWordsPage findMatches(String text, String words) {
        type(xpath("//h3[normalize-space()='Текст']/following::textarea[1]"), text);
        type(xpath("//h3[contains(normalize-space(),'Слова для поиска')]/following::textarea[1]"), words);
        click(xpath("//div[contains(@class,'re_botmenu')]//a[normalize-space()='Найти']"));
        waitForText(totalMatchesLocator(), value -> !value.isBlank());
        return this;
    }

    public String matchesCount() {
        return text(totalMatchesLocator());
    }

    public boolean hasResultRow(String word) {
        return isVisible(xpath("//h3[normalize-space()='Совпадения']/following::table[1]//span[normalize-space()='" + word + "']"));
    }

    public int highlightedMatches() {
        return count(xpath("//div[contains(@style,'border:1') and .//span[@fw]]//span[@fw]"));
    }

    private org.openqa.selenium.By totalMatchesLocator() {
        return xpath("//table[contains(@class,'seo_table')]//tr[td[1]//span[normalize-space()='Всего совпадений']]/td[last()]");
    }
}
