package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class MyArticlesPage extends BasePage {
    public MyArticlesPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public MyArticlesPage openAllArticles() {
        openAuthorizedRelative("/shop/my/wares/");
        waitForVisible(xpath("//*[contains(normalize-space(),'Мои статьи')]"));
        return this;
    }

    public MyArticlesPage openShowcase() {
        openAuthorizedRelative("/shop/my/showcase/");
        waitForVisible(xpath("//*[contains(normalize-space(),'Статьи на продаже')]"));
        return this;
    }

    public boolean containsArticleTitle(String title) {
        return pageSource().contains(title);
    }
}
