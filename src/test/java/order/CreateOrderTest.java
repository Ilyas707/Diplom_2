package order;

import data.Order;
import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;
import java.util.ArrayList;
import java.util.List;
import static order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class CreateOrderTest {

    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private Order order;
    private String bearerToken;

    @Before
    @Step("Подготовка данных для теста")
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        order = getListOrder();
        orderClient = new OrderClient();
    }

    @Step("Регистрация и авторизация пользователя")
    private void registerAndLogin() {
        ValidatableResponse responseRegister = userClient.register(user);
        responseRegister.assertThat().statusCode(SC_OK);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");
    }

    @Test
    @Step("Создание заказа с авторизацией")
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с авторизацией")
    public void createOrderWithAuthorizationTest() {
        registerAndLogin();
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Step("Создание заказа без авторизации")
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        bearerToken = "";
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Step("Создание заказа без ингредиентов")
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка создания заказа без ингредиентов")
    public void createOrderWithoutIngredientTest() {
        registerAndLogin();
        order.setIngredients(java.util.Collections.emptyList());
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа с неверным хешем ингредиентов")
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка создания заказа с неправильными ингредиентами")
    public void createOrderWithInvalidIngredientTest() {
        registerAndLogin();
        List<String> wrongIngredient = new ArrayList<>();
        wrongIngredient.add("58d3b41abdacab1236a662c6");
        order.setIngredients(wrongIngredient);
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .and()
                .body("message", is("One or more ids provided are incorrect"));
    }

    @Step("Удаление пользователя после теста")
    @After
    public void tearDown() {
        if (bearerToken == null || bearerToken.isEmpty()) return;
        userClient.delete(bearerToken);
    }
}
