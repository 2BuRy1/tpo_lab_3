package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class CampaignOrdersPage extends BasePage {
    public CampaignOrdersPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public CampaignOrdersPage open() {
        openAuthorizedRelative("/campaigns/orders/all/");
        wait.until(driver -> currentUrl().contains("/campaigns/orders/")
                || count(xpath("//a[contains(@href,'/order/add/')]")) > 0
                || pageSource().contains("/campaigns/orders/"));
        return this;
    }

    public boolean hasHeading() {
        return count(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Все заказы')]")) > 0
                || currentUrl().contains("/campaigns/orders/");
    }

    public boolean hasCreateOrderLink() {
        return count(xpath("//a[contains(@href,'/order/add/')]")) > 0;
    }

    public boolean hasTabs() {
        return isVisible(xpath("//div[contains(@class,'re_h2menu')]//a[contains(@href,'/campaigns/orders/active/')]"))
                && isVisible(xpath("//div[contains(@class,'re_h2menu')]//a[contains(@href,'/campaigns/orders/deleted/')]"))
                && isVisible(xpath("//div[contains(@class,'re_h2menu')]//a[contains(@href,'/campaigns/orders/no_authors/')]"));
    }

    public boolean showsEmptyState() {
        return isVisible(xpath("//div[contains(@class,'list_item')][contains(.,'Нет заказов.')]"));
    }

    public boolean containsOrderTitle(String title) {
        return pageSource().contains(title);
    }
}
