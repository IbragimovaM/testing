import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.util.concurrent.TimeUnit;

public class TestBase {
    private WebDriver driver;
    WebDriverWait wait;
    private static final String SITE_URL = "http://e-katalog.ru";

    @BeforeMethod
    protected void setUp() {
        setDriver(new ChromeDriver());
        wait = new WebDriverWait(driver, 15);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(SITE_URL);
    }

    @AfterMethod
    protected void tearDown() {
        getDriver().quit();
    }

    @Step("Вход в аккаунт. Логин: {0}, пароль: {1}")
    void logIn(String login, String password) {
        final By loginBtnSelector = By.cssSelector("[jid=\"mui_user_login_window\"]");
        WebElement loginBtn = getDriver().findElement(loginBtnSelector);
        loginBtn.click();

        final WebElement loginField = getDriver().findElement(By.cssSelector("#mui_user_login_window_avt input[name=\"l_\"]"));
        final WebElement passwordField = getDriver().findElement(By.cssSelector("#mui_user_login_window_avt input[name=\"pw_\"]"));

        loginField.sendKeys(login);
        passwordField.sendKeys(password);

        final WebElement loginButton = getDriver().findElement(By.cssSelector("#mui_user_login_window_avt input[type=\"submit\"]"));
        loginButton.click();

        final By loginSelector = By.cssSelector("#mui_user_login_row .info-nick");

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(loginSelector));
        } catch (TimeoutException e) {
            throw new AssertionError("Login unsuccessful: username has not appeared");
        }

        final WebElement loggedUser = getDriver().findElement(loginSelector);
        Assert.assertEquals(loggedUser.getText(), login, "Logged in user does not match");
    }

    @Step("Выход из аккаунта")
    void logOut() {
        final WebElement logoutButton = getDriver().findElement(By.cssSelector("#mui_user_login_row .ib"));
        this.scrollIntoView(logoutButton);
        logoutButton.click();

        By loginBtnSelector = By.cssSelector("[jid=\"mui_user_login_window\"]");

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(loginBtnSelector));
        } catch (TimeoutException e) {
            throw new AssertionError("Login button has not displayed");
        }
    }

    void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    void click(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by));

        WebElement element = driver.findElement(by);
        this.scrollIntoView(element);

        for (int i = 0; i < 10; i++) {
            try {
                new Actions(driver)
                        .click(element)
                        .perform();

                break;
            } catch (StaleElementReferenceException exception) {
                System.err.println("Failed to click element, " + (i + 1) + " tries total.");
                exception.printStackTrace();
            }
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}