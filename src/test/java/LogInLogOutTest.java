import io.qameta.allure.Description;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class LogInLogOutTest extends TestBase {

    @Test
    @Description("Вход в аккаунт")
    @Parameters({"login", "password"})
    public void logInLogOutTest(String login, String password) {
        this.logIn(login, password);
        this.logOut();
    }
}
