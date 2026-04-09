package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class SeoAnalysisPage extends BasePage {
    public SeoAnalysisPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public SeoAnalysisPage open() {
        openRelative("/text/seo/");
        waitForVisible(xpath("//form[contains(@action,'/text/seo/') and contains(@class,'action_form')]//textarea[@name='job_text']"));
        return this;
    }

    public SeoAnalysisPage analyze(String text) {
        type(xpath("//form[contains(@action,'/text/seo/') and contains(@class,'action_form')]//textarea[@name='job_text']"), text);
        click(xpath("//div[contains(@class,'botmenu')]//a[normalize-space()='Проверить']"));
        waitForText(statValueLocator("Количество символов"), value -> !value.isBlank());
        return this;
    }

    public String statValue(String label) {
        return text(statValueLocator(label));
    }

    public String wordCount(String word) {
        return text(xpath("//h3[normalize-space()='Слова']/following-sibling::div[1]//tr[td[1][normalize-space()='" + word + "']]/td[2]"));
    }

    public String wordFrequency(String word) {
        return text(xpath("//h3[normalize-space()='Слова']/following-sibling::div[1]//tr[td[1][normalize-space()='" + word + "']]/td[3]"));
    }

    public String languageSummary() {
        return text(xpath("//div[contains(@class,'job_desc')][.//b[contains(normalize-space(),'Язык')]]"));
    }

    public String analyzedText() {
        return text(xpath("//h3[normalize-space()='Текст']/following-sibling::div[contains(@class,'job_desc')][1]"));
    }

    private org.openqa.selenium.By statValueLocator(String label) {
        return xpath("//table[contains(@class,'seo_table')][.//th[contains(normalize-space(),'Наименование показателя')]]//tr[td[1][normalize-space()='" + label + "']]/td[last()]");
    }
}
