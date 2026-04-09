package ru.itmo.tpo.advego.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import ru.itmo.tpo.advego.core.BasePage;

public class ProfilePage extends BasePage {
    public ProfilePage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout, baseUrl);
    }

    public ProfilePage open() {
        openAuthorizedRelative("/user/");
        wait.until(driver -> currentUrl().contains("/user/")
                || count(xpath("//a[@id='user_form_submit']")) > 0
                || pageSource().contains("user_form_submit"));
        return this;
    }

    public boolean hasHeading() {
        return count(xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Личные данные') or contains(normalize-space(),'Персональные данные') or contains(normalize-space(),'Профиль')]")) > 0
                || pageSource().contains("user_form_submit");
    }

    public boolean hasAboutAndEducationRestriction() {
        return isVisible(xpath("//p[contains(.,'Поля \"Образование\" и \"О себе\" будут доступны для заполнения после выполнения 50 работ')]"));
    }

    public boolean isAboutFieldDisabled() {
        return isDisabledPresent(xpath("//textarea[@id='user_profession']"));
    }

    public boolean isEducationFieldDisabled() {
        return isDisabledPresent(xpath("(//textarea[@id='user_education'])[1]"));
    }

    public boolean hasSaveButton() {
        return count(xpath("//a[@id='user_form_submit']")) > 0;
    }

    public String timezone() {
        return selectedOptionText(xpath("//select[@id='user_time']"));
    }

    public boolean isEuropeDstSelected() {
        return waitForPresent(xpath("//input[@id='user_dst_0']")).isSelected();
    }

    public boolean showsOrderPriceWithoutCommissionOptionSelected() {
        return waitForPresent(xpath("//input[@id='user_no_my_order_comission_1']")).isSelected();
    }
}
