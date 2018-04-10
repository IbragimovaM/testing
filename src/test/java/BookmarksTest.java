import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class BookmarksTest extends TestBase {

    @Step("Переход в категорию и применение фильтров")
    private void locateCategoryAndApplyFilters() {
        By gadgets = By.linkText("Гаджеты");
        this.click(gadgets);

        By actionCameras = By.linkText("Action камеры");
        this.click(actionCameras);

        By sonyFilterEl = By.cssSelector("label[for=\"br156\"]");
        this.click(sonyFilterEl);

        final By nfcFilterEl = By.cssSelector("label[for=\"c17983\"]");
        this.click(nfcFilterEl);

        final By showModels = By.cssSelector("a.show-models");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(showModels));
        } catch (TimeoutException e) {
            throw new AssertionError("Show results popup has not displayed");
        }

        this.click(showModels);
    }

    @Step("Поиск товара {0}")
    private void checkItemAndAddBookmark(String itemName) {
        By itemLink = By.linkText(itemName);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(itemLink));
        } catch (TimeoutException e) {
            throw new AssertionError("Item " + itemName + " not found in search results");
        }

        WebElement item = getDriver().findElement(itemLink);
        new Actions(getDriver())
                .moveToElement(item)
                .click()
                .perform();

        By addBookmark = By.cssSelector(".big-star-off.off");
        this.click(addBookmark);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".big-star-on.on")));
        } catch (TimeoutException e) {
            throw new AssertionError("Failed to add the test item to bookmarks");
        }
    }

    @Step("Проверка наличия {0} в истории просмотров")
    private void checkHistory(String itemName) {
        By historyBtn = By.id("bar_bm_visited");
        this.click(historyBtn);

        By historyItem = By.partialLinkText(itemName);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(historyItem));
        } catch (TimeoutException|ElementNotFoundException e) {
            throw new AssertionError("Item is not found in view history");
        }

        this.click(historyBtn);
    }

    @Step("Проверка наличия {0} в закладках")
    private void checkAndRemoveBookmark(String itemName) {
        By bookmarksBtn = By.id("bar_bm_marked");
        this.click(bookmarksBtn);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(itemName)));
        } catch (TimeoutException|ElementNotFoundException e) {
            throw new AssertionError("Item is not found in bookmarks");
        }

        By removeBookmark = By.cssSelector(".big-star-on.on");
        this.click(removeBookmark);
        this.click(bookmarksBtn);
    }

    @Test
    @Description("Добавление товара {2} в закладки и история посещений")
    @Parameters({"login", "password", "itemName"})
    public void bookmarksTest(String login, String password, String itemName) {
        this.logIn(login, password);
        this.locateCategoryAndApplyFilters();
        this.checkItemAndAddBookmark(itemName);
        this.checkHistory(itemName);
        this.checkAndRemoveBookmark(itemName);
        this.logOut();
    }
}
