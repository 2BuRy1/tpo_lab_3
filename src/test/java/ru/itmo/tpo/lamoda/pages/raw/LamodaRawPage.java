package ru.itmo.tpo.lamoda.pages.raw;

import java.time.Duration;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.lamoda.core.BasePage;

public class LamodaRawPage extends BasePage {
    public enum AccessResult {
        OPENED,
        QRATOR_BLOCK,
        NETWORK_BLOCK
    }

    public LamodaRawPage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public LamodaRawPage openPath(String path) {
        openRelative(path);
        return this;
    }

    public boolean isBlockedByQrator() {
        return isAntiBotPage();
    }

    public AccessResult probePath(String path) {
        try {
            openRelative(path);
            return isAntiBotPage() ? AccessResult.QRATOR_BLOCK : AccessResult.OPENED;
        } catch (WebDriverException ex) {
            if (isFirefoxNetworkBlock(ex)) {
                return AccessResult.NETWORK_BLOCK;
            }
            throw ex;
        }
    }

    private boolean isFirefoxNetworkBlock(WebDriverException ex) {
        String message = ex.getMessage();
        return message != null && message.contains("about:neterror?e=nssFailure2");
    }
}
