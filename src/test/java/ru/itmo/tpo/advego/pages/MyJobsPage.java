package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class MyJobsPage extends BasePage {
    public MyJobsPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public MyJobsPage open() {
        openAuthorizedRelative("/job/my/");
        wait.until(driver -> currentUrl().contains("/job/my/")
                || count(xpath("//a[contains(@href,'/job/find/')]")) > 0
                || count(xpath("//a[contains(@href,'/shop/sell/')]")) > 0
                || pageSource().contains("/job/find/"));
        return this;
    }

    public boolean hasHeading() {
        return isVisible(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Активные работы') or contains(normalize-space(),'Мои работы')]"));
    }

    public boolean hasIncomeSummaryTable() {
        return isVisible(xpath("//table[contains(@class,'purse') or contains(@class,'table')]//*[contains(normalize-space(),'Доход')]"))
                || pageSource().contains("Доход");
    }

    public boolean hasFindWorkLink() {
        return count(xpath("//a[contains(@href,'/job/find/')]")) > 0;
    }

    public boolean hasSellArticleLink() {
        return count(xpath("//a[contains(@href,'/shop/sell/')]")) > 0;
    }
}
