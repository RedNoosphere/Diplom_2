package praktikum;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import java.util.HashMap;
import java.util.Map;
import net.datafaker.Faker;

public class BaseApiTest {

    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    protected Map<String, String> testUsers = new HashMap<>();
    protected Faker faker;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        faker = new Faker();
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
        return faker.internet().emailAddress();
    }

    protected String registerTestUser(String email, String password, String name) {
        // Создаем объект для сериализации вместо строки JSON
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("name", name);

        Response response = post("/auth/register", requestBody);
        String accessToken = response.path("accessToken");
        testUsers.put(email, accessToken);

        return accessToken;
    }

    // Дополнительные методы для генерации тестовых данных через Faker
    protected String generateRandomName() {
        return faker.name().firstName();
    }

    protected String generateRandomPassword() {
        return faker.internet().password(8, 16, true, true, true);
    }
}