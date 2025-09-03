package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

@Epic("Stellar Burgers API")
@Feature("Авторизация пользователя")
@DisplayName("Тесты логина пользователя")
public class UserLoginTest extends BaseApiTest {

    private String testEmail;
    private String testPassword;
    private String testName;

    @Before
    public void setUp() {
        super.setUp(); // Вызываем родительский setUp
        // Создаем тестового пользователя перед каждым тестом
        testEmail = generateUniqueEmail();
        testPassword = "testpassword123";
        testName = "Test User";
        registerTestUser(testEmail, testPassword, testName);
    }

    @After
    public void tearDown() {
        // Очищаем созданного пользователя после каждого теста
        if (testEmail != null && testUsers.containsKey(testEmail)) {
            String token = testUsers.get(testEmail);
            if (token != null) {
                deleteUser(token);
                testUsers.remove(testEmail);
            }
        }
        testEmail = null;
        testPassword = null;
        testName = null;
    }

    @Test
    @DisplayName("Успешный логин под существующим пользователем")
    @Description("Проверка успешной авторизации существующего пользователя с правильными учетными данными")
    public void testLoginWithExistingUser() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", testEmail);
        requestBody.put("password", testPassword);

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testEmail))
                .body("user.name", equalTo(testName))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка обработки ошибки при авторизации с неверным паролем")
    public void testLoginWithWrongPassword() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", testEmail);
        requestBody.put("password", "wrongpassword");

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с несуществующим пользователем")
    @Description("Проверка обработки ошибки при авторизации несуществующего пользователя")
    public void testLoginWithNonExistentUser() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", "nonexistent@yandex.ru");
        requestBody.put("password", "password123");

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин без email")
    @Description("Проверка обработки ошибки при авторизации без указания email")
    public void testLoginWithoutEmail() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("password", "password123");

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин без пароля")
    @Description("Проверка обработки ошибки при авторизации без указания пароля")
    public void testLoginWithoutPassword() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", "test@yandex.ru");

        Response response = post("/auth/login", requestBody);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}