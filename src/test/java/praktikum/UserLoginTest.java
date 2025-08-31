package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

@Epic("Stellar Burgers API")
@Feature("Авторизация пользователя")
@DisplayName("Тесты логина пользователя")
public class UserLoginTest extends BaseApiTest {

    @Test
    @DisplayName("Успешный логин под существующим пользователем")
    public void testLoginWithExistingUser() {
        // Сначала создаем пользователя
        String email = generateUniqueEmail();
        String password = "testpassword123";
        registerTestUser(email, password, "Test User");

        // Логинимся
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                email, password
        );

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo("Test User"))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void testLoginWithWrongPassword() {
        String email = generateUniqueEmail();
        registerTestUser(email, "correctpassword", "Test User");

        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"wrongpassword\"}",
                email
        );

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с несуществующим пользователем")
    public void testLoginWithNonExistentUser() {
        String requestBody = "{\"email\": \"nonexistent@yandex.ru\", \"password\": \"password123\"}";

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин без email")
    public void testLoginWithoutEmail() {
        String requestBody = "{\"password\": \"password123\"}";

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин без пароля")
    public void testLoginWithoutPassword() {
        String requestBody = "{\"email\": \"test@yandex.ru\"}";

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}