package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.After;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

@Epic("Stellar Burgers API")
@Feature("Регистрация пользователя")
@DisplayName("Тесты регистрации пользователя")
public class UserRegistrationTest extends BaseApiTest {

    private String createdUserEmail;

    @After
    public void cleanUp() {
        if (createdUserEmail != null) {
            String token = testUsers.get(createdUserEmail);
            if (token != null) {
                deleteUser(token);
                testUsers.remove(createdUserEmail);
            }
            createdUserEmail = null;
        }
    }

    @Test
    @DisplayName("Успешная регистрация уникального пользователя")
    @Description("Проверка успешной регистрации нового пользователя с валидными данными")
    public void testCreateUniqueUser() {
        String email = generateUniqueEmail();
        createdUserEmail = email;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", "password123");
        requestBody.put("name", "Test User");

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo("Test User"))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    @Description("Проверка обработки ошибки при попытке регистрации пользователя с уже существующим email")
    public void testCreateExistingUser() {
        // Сначала создаем пользователя
        String email = generateUniqueEmail();
        createdUserEmail = email;
        registerTestUser(email, "password123", "Existing User");

        // Пытаемся создать again
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", "password123");
        requestBody.put("name", "Duplicate User");

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без email")
    @Description("Проверка обработки ошибки при регистрации без указания email")
    public void testCreateUserWithoutEmail() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("password", "password123");
        requestBody.put("name", "Test User");

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без пароля")
    @Description("Проверка обработки ошибки при регистрации без указания пароля")
    public void testCreateUserWithoutPassword() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", "test@yandex.ru");
        requestBody.put("name", "Test User");

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без имени")
    @Description("Проверка обработки ошибки при регистрации без указания имени")
    public void testCreateUserWithoutName() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", "test@yandex.ru");
        requestBody.put("password", "password123");

        Response response = post("/auth/register", requestBody);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}