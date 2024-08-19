package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class LoginUserTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка авторизации под существующим пользователем")
    public void loginUserTest() {
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @DisplayName("Логин с неверным логином")
    @Description("Проверка авторизации с неверным логином")
    public void loginWithIncorrectEmailTest() {
        user.setEmail("dfsa@mail.com");  // Неверный email
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка авторизации с неверным паролем")
    public void loginWithIncorrectPasswordTest() {
        user.setPassword("safdasdf");  // Неверный пароль
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (bearerToken == null || bearerToken.isEmpty()) return;
        userClient.delete(bearerToken);
    }
}
