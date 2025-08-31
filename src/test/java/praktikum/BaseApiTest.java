package praktikum;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseApiTest {

    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    protected Map<String, String> testUsers = new HashMap<>();

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @After
    public void tearDown() {
        // Очистка созданных тестовых пользователей
        for (String token : testUsers.values()) {
            if (token != null) {
                deleteUser(token);
            }
        }
        testUsers.clear();
    }

    protected Response get(String endpoint) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get(endpoint);
    }

    protected Response post(String endpoint, Object body) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post(endpoint);
    }

    protected Response postWithAuth(String endpoint, Object body, String token) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(body)
                .when()
                .post(endpoint);
    }

    protected void deleteUser(String token) {
        try {
            RestAssured.given()
                    .header("Content-type", "application/json")
                    .header("Authorization", token)
                    .when()
                    .delete("/auth/user");
        } catch (Exception e) {
            // Игнорируем ошибки при удалении (пользователь может уже не существовать)
        }
    }

    protected String generateUniqueEmail() {
        return "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@yandex.ru";
    }

    protected String registerTestUser(String email, String password, String name) {
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}",
                email, password, name
        );

        Response response = post("/auth/register", requestBody);
        testUsers.put(email, response.path("accessToken"));

        return response.path("accessToken");
    }
}