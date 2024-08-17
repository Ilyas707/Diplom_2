package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class CreateUserTest {

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
    @Step("Создание уникального пользователя")
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создания уникального пользователя")
    public void createUserTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        responseRegister.assertThat().statusCode(SC_OK).body("success", is(true));
        bearerToken = responseRegister.extract().path("accessToken");
    }

    @Test
    @Step("Создание пользователя, который уже зарегистрирован")
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка создания пользователя, который уже зарегистрирован")
    public void createDuplicateUserTest() {
        ValidatableResponse responseRegisterFirstUser = userClient.register(user);
        bearerToken = responseRegisterFirstUser.extract().path("accessToken");

        ValidatableResponse responseRegisterSecondUser = userClient.register(user);
        responseRegisterSecondUser.assertThat().statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @Step("Создание пользователя без заполнения поля Имя")
    @DisplayName("Создание пользователя без заполнения поля Имя")
    @Description("Проверка создания пользователя без заполнения поля Имя")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse responseRegister = userClient.register(user);
        responseRegister.assertThat().statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Step("Создание пользователя без заполнения поля Email")
    @DisplayName("Создание пользователя без заполнения поля Email")
    @Description("Проверка создания пользователя без заполнения поля Email")
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse responseRegister = userClient.register(user);
        responseRegister.assertThat().statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Step("Создание пользователя без заполнения поля Пароль")
    @DisplayName("Создание пользователя без заполнения поля Пароль")
    @Description("Проверка создания пользователя без заполнения поля Пароль")
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse responseRegister = userClient.register(user);
        responseRegister.assertThat().statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Step("Удаление пользователя после теста")
    @After
    public void tearDown() {
        if (bearerToken == null || bearerToken.isEmpty()) return;
        userClient.delete(bearerToken);
    }
}
