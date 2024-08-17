package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class UpdateUserTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    @Step("Подготовка данных для теста")
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
    }

    @Test
    @Step("Изменение данных пользователя с авторизацией")
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверка изменения данных пользователя с авторизацией")
    public void updateUserWithAuthorizationTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        if (responseRegister.extract().path("success").equals(true)) {
            bearerToken = responseRegister.extract().path("accessToken");
            User secondUser = getRandomUser();
            ValidatableResponse responsePatch = userClient.patch(secondUser, bearerToken);
            responsePatch.assertThat().statusCode(SC_OK).body("success", is(true));
        } else {
            throw new RuntimeException("Регистрация пользователя не удалась");
        }
    }

    @Test
    @Step("Изменение данных пользователя без авторизации")
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка изменения данных пользователя без авторизации")
    public void updateUserWithoutAuthorizationTest() {
        User secondUser = getRandomUser();
        ValidatableResponse responsePatch = userClient.patch(secondUser, "");
        responsePatch.assertThat().statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Step("Удаление пользователя после теста")
    @After
    public void tearDown() {
        if (bearerToken != null && !bearerToken.isEmpty()) {
            userClient.delete(bearerToken);
        }
    }
}
