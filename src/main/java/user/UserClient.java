package user;

import utilities.ApiEndpoints;
import utilities.Specifications;
import data.User;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.Matchers.is;

public class UserClient {
    @Step("Отправить post-запрос в api/auth/register")
    public ValidatableResponse register(User user) {
        return given()
                .spec(Specifications.requestSpecification())
                .and()
                .body(user)
                .when()
                .post(ApiEndpoints.REGISTER_USER_API)
                .then();
    }

    @Step("Отправить post-запрос в api/auth/login")
    public ValidatableResponse login(User user) {
        return given()
                .spec(Specifications.requestSpecification())
                .and()
                .body(user)
                .when()
                .post(ApiEndpoints.LOGIN_API)
                .then();
    }

    @Step("Отправить delete-запрос в api/auth/user")
    public ValidatableResponse delete(String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers("Authorization", bearerToken)
                .delete(ApiEndpoints.DELETE_USER_API)
                .then()
                .statusCode(SC_ACCEPTED)
                .and()
                .body("message", is("User successfully removed"));
    }

    @Step("Отправить patch-запрос в api/auth/user")
    public ValidatableResponse patch(User user, String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .header("Authorization", bearerToken)
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .patch(ApiEndpoints.PATCH_USER_API)
                .then();
    }
}
