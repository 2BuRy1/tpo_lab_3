package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class SellArticlePage extends BasePage {
    public SellArticlePage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public SellArticlePage open() {
        openAuthorizedRelative("/shop/sell/");
        wait.until(driver -> currentUrl().contains("/shop/sell/")
                || count(xpath("//form[@id='text_add_form']")) > 0
                || pageSource().contains("text_add_form"));
        return this;
    }

    public boolean hasHeading() {
        return count(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Продать статью')]")) > 0
                || currentUrl().contains("/shop/sell/");
    }

    public boolean hasForm() {
        return count(xpath("//form[@id='text_add_form']")) > 0;
    }

    public boolean hasTitleField() {
        return isVisible(xpath("(//textarea[@id='text_title'])[1]"));
    }

    public boolean hasArticleTextField() {
        return isVisible(xpath("(//textarea[@id='job_text'])[1]"));
    }

    public boolean hasCostFields() {
        return isVisible(xpath("//input[@id='shop_text_cost']"))
                && isVisible(xpath("//input[@id='shop_text_cost_1000']"));
    }

    public boolean isDoNotPublishCheckedByDefault() {
        return waitForPresent(xpath("//input[@id='text_not_publish']")).isSelected();
    }

    public int disclaimerCheckboxesCount() {
        return count(xpath("//form[@id='text_add_form']//input[starts-with(@id,'agree_') and @id != 'agree_all']"));
    }

    public boolean hasAgreeAllCheckbox() {
        return count(xpath("//input[@id='agree_all']")) > 0;
    }

    public boolean hasGrammarGateBlock() {
        return count(xpath("//div[@id='grammardiv_id']")) > 0;
    }

    public boolean hasPublicationErrorBlock() {
        return count(xpath("//div[contains(@class,'jd_disc_msg')]")) > 0;
    }

    public SellArticlePage enterTitle(String title) {
        type(xpath("(//textarea[@id='text_title'])[1]"), title);
        return this;
    }

    public SellArticlePage selectCategory(String category) {
        selectByVisibleText(xpath("//select[@id='id_cat']"), category);
        return this;
    }

    public SellArticlePage selectTextType(String textType) {
        selectByVisibleText(xpath("//select[@id='text_type']"), textType);
        return this;
    }

    public SellArticlePage enterArticleText(String text) {
        type(xpath("(//textarea[@id='job_text'])[1]"), text);
        return this;
    }

    public SellArticlePage generateShortDescription() {
        click(xpath("//span[contains(@class,'bbcode_button') and normalize-space()='Краткий текст']"));
        waitForText(xpath("//span[@id='text_description_length']"), value -> value != null && value.matches(".*\\d+.*"));
        return this;
    }

    public SellArticlePage enterNotes(String notes) {
        type(xpath("(//textarea[@id='text_notes'])[1]"), notes);
        return this;
    }

    public SellArticlePage enterPrice(String price) {
        type(xpath("//input[@id='shop_text_cost']"), price);
        return this;
    }

    public SellArticlePage setPublishToMarketplace(boolean publish) {
        setCheckbox(xpath("//input[@id='text_not_publish']"), !publish);
        return this;
    }

    public SellArticlePage agreeToAllTerms() {
        setCheckbox(xpath("//input[@id='agree_all']"), true);
        return this;
    }

    public void submitArticle() {
        click(xpath("//div[contains(@class,'botmenu')]//a[normalize-space()='Добавить статью']"));
    }

    public void waitForSubmissionOutcome() {
        wait.until(driver -> {
            boolean grammarGateVisible = driver.findElements(xpath("//div[@id='grammardiv_id']")).stream().anyMatch(element -> {
                try {
                    return element.isDisplayed();
                } catch (Exception ex) {
                    return false;
                }
            });
            return grammarGateVisible || !driver.getCurrentUrl().contains("/shop/sell/");
        });
    }

    public boolean isGrammarGateVisible() {
        return isVisible(xpath("//div[@id='grammardiv_id' and not(contains(@style,'display:none'))]"));
    }
}
