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
import static order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class GetOrderTest {
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
    @Step("Получение заказов авторизованного пользователя")
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка получения заказов авторизованного пользователя")
    public void getOrdersWithAuthorizationTest() {
        registerAndLogin();
        orderClient.create(order, bearerToken);
        ValidatableResponse responseOrderUser = orderClient.getClientOrder(bearerToken);
        responseOrderUser.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Step("Получение заказов неавторизованного пользователя")
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Проверка получения заказов неавторизованного пользователя")
    public void getOrdersWithoutAuthorizationTest() {
        bearerToken = "";
        ValidatableResponse getClientOrder = orderClient.getClientOrder(bearerToken);
        getClientOrder.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false))
                .and().body("message", is("You should be authorised"));
    }

    @Step("Удаление пользователя после теста")
    @After
    public void tearDown() {
        if (bearerToken == null || bearerToken.isEmpty()) return;
        userClient.delete(bearerToken);
    }
}
