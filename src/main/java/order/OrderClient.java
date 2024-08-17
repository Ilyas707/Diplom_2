package order;

import utilities.ApiEndpoints;
import utilities.Specifications;
import data.Order;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderClient {
    @Step("Отправить post-запрос в api/orders")
    public ValidatableResponse create(Order order, String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers("Authorization", bearerToken)
                .and()
                .body(order)
                .when()
                .post(ApiEndpoints.CREATE_ORDER_API)
                .then();
    }

    @Step("Отправить get-запрос в api/ingredients/")
    public static ValidatableResponse getAllIngredients() {
        return given()
                .spec(Specifications.requestSpecification())
                .get(ApiEndpoints.INGREDIENT_API)
                .then();
    }

    @Step("Отправить get-запрос в api/orders")
    public static ValidatableResponse getClientOrder(String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers("Authorization", bearerToken)
                .get(ApiEndpoints.USER_ORDERS_API)
                .then();
    }
}
