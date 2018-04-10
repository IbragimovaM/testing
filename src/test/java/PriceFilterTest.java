import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(TestListener.class)
public class PriceFilterTest extends TestBase {

    @Step("Переход в категорию и поиск с ограничением цены до {0} рублей")
    private void locateCategoryAndApplyFilters(int maxPrice) {
        By computers = By.linkText("Компьютеры");
        this.click(computers);

        By tablets = By.linkText("Планшеты");
        this.click(tablets);

        final WebElement priceUpperLimitField = getDriver().findElement(By.id("maxPrice_"));
        wait.until(ExpectedConditions.visibilityOf(priceUpperLimitField));

        priceUpperLimitField.sendKeys(String.valueOf(maxPrice));

        By showModels = By.className("show-models");
        this.click(showModels);
    }

    @Step("Проверка цен товаров в выдаче (до {0} рублей)")
    private void checkPrices(int maxPrice) {
        By lowPrice = By.cssSelector(".model-price-range > a > span:first-child");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(lowPrice));
        } catch (TimeoutException|ElementNotFoundException e) {
            throw new AssertionError("Result list is empty");
        }

        List<WebElement> prices = getDriver().findElements(By.cssSelector(".model-price-range > a > span:first-child"));

        for (WebElement e : prices) {
            int price = Integer.valueOf(e.getText().replaceAll("\\D+", ""));

            if (price > maxPrice)
                throw new AssertionError("One of the results has min price greater than search price: "
                        + price);
        }
    }

    @Test
    @Description("Поиск с ограничением по цене")
    @Parameters("maxPrice")
    public void priceFilterTest(int maxPrice) {
        this.locateCategoryAndApplyFilters(maxPrice);
        this.checkPrices(maxPrice);
    }
}
