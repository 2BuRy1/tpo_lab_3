package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class JobFindPage extends BasePage {
    private static final Pattern HEADING_COUNT_PATTERN = Pattern.compile("Все заказы\\s*/\\s*(\\d+)");
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("(\\d{5,})");

    public JobFindPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public JobFindPage open() {
        openRelative("/job/find/", false);

        if (isVisible(xpath("//a[@id='lets_work' and contains(normalize-space(),'Начать зарабатывать')]"))) {
            click(xpath("//a[@id='lets_work' and contains(normalize-space(),'Начать зарабатывать')]"));
            loginThroughModalStupidly();
        } else {
            loginThroughModalStupidly();
        }

        openRelative("/job/find/", false);

        wait.until(driver -> currentUrl().contains("/job/find/")
                || count(xpath(orderCardsXPath())) > 0
                || pageSource().contains("/job/"));

        return this;
    }

    public int ordersInHeading() {
        Matcher matcher = HEADING_COUNT_PATTERN.matcher(text(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Все заказы') or contains(normalize-space(),'Доступные заказы')]")));
        if (!matcher.find()) {
            throw new IllegalStateException("Не удалось извлечь количество заказов из заголовка списка работ.");
        }
        return Integer.parseInt(matcher.group(1));
    }

    public int visibleOrdersCount() {
        return count(xpath(orderCardsXPath()));
    }

    public String firstOrderId() {
        int linksInCard = count(xpath("(" + orderCardsXPath() + ")[1]//a[@href]"));
        for (int i = 1; i <= linksInCard; i++) {
            String href = attribute(xpath("((" + orderCardsXPath() + ")[1]//a[@href])[" + i + "]"), "href");
            Matcher hrefMatcher = ORDER_ID_PATTERN.matcher(href == null ? "" : href);
            if (hrefMatcher.find()) {
                return hrefMatcher.group(1);
            }
        }

        Matcher textMatcher = ORDER_ID_PATTERN.matcher(firstOrderCardText());
        if (textMatcher.find()) {
            return textMatcher.group(1);
        }

        throw new IllegalStateException("Не удалось извлечь числовой идентификатор первого заказа из карточки.");
    }

    public String firstOrderType() {
        return text(xpath("(" + orderCardsXPath() + ")[1]//*[contains(@class,'order-type') or contains(@class,'type')][1]"));
    }

    public String firstOrderTitle() {
        return text(xpath("(" + orderCardsXPath() + ")[1]//a[contains(@href,'/job/') or contains(@href,'/order/')][normalize-space()][1]"));
    }

    public String firstOrderPrice() {
        Matcher matcher = Pattern.compile("\\d+[\\d.,]*\\s*руб\\.?").matcher(firstOrderCardText());
        if (!matcher.find()) {
            throw new IllegalStateException("Не удалось извлечь цену первого заказа из карточки.");
        }
        return matcher.group();
    }

    public String firstOrderPricePerThousand() {
        Matcher matcher = Pattern.compile("\\d+[\\d.,]*\\s*руб\\.?[^\\n]*1000|1000[^\\n]*\\d+[\\d.,]*\\s*руб\\.?", Pattern.CASE_INSENSITIVE).matcher(firstOrderCardText());
        return matcher.find() ? matcher.group() : firstOrderPrice();
    }

    public boolean firstOrderHasTakeToWorkAction() {
        return count(xpath("(" + orderCardsXPath() + ")[1]//a[contains(normalize-space(),'Взять') or contains(normalize-space(),'Подать') or contains(normalize-space(),'Заявк') or contains(normalize-space(),'работ')]")) > 0;
    }

    public boolean firstOrderHasSecretDescription() {
        String text = firstOrderCardText().toLowerCase();
        return text.contains("секрет") || text.contains("описан");
    }

    private String firstOrderCardText() {
        return waitForVisible(xpath("(" + orderCardsXPath() + ")[1]")).getText().trim();
    }

    private String orderCardsXPath() {
        return "//*[contains(@class,'job_row') or contains(@class,'jobs_row') or contains(@class,'list_item')][.//a[contains(@href,'/job/') or contains(@href,'/order/')]][.//*[contains(normalize-space(),'Взять') or contains(normalize-space(),'Заявк') or contains(normalize-space(),'руб') or contains(normalize-space(),'₽')]]";
    }
}
