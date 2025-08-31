package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

@Epic("Stellar Burgers API")
@Feature("Регистрация пользователя")
@DisplayName("Тесты регистрации пользователя")
public class UserRegistrationTest extends BaseApiTest {

    @Test
    @DisplayName("Успешная регистрация уникального пользователя")
    public void testCreateUniqueUser() {
        String email = generateUniqueEmail();
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"password123\", \"name\": \"Test User\"}",
                email
        );

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo("Test User"))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    public void testCreateExistingUser() {
        // Сначала создаем пользователя
        String email = generateUniqueEmail();
        registerTestUser(email, "password123", "Existing User");

        // Пытаемся создать again
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"password123\", \"name\": \"Duplicate User\"}",
                email
        );

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без email")
    public void testCreateUserWithoutEmail() {
        String requestBody = "{\"password\": \"password123\", \"name\": \"Test User\"}";

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без пароля")
    public void testCreateUserWithoutPassword() {
        String requestBody = "{\"email\": \"test@yandex.ru\", \"name\": \"Test User\"}";

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без имени")
    public void testCreateUserWithoutName() {
        String requestBody = "{\"email\": \"test@yandex.ru\", \"password\": \"password123\"}";

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}