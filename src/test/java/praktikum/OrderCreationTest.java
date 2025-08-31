package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

@Epic("Stellar Burgers API")
@Feature("Создание заказов")
@DisplayName("Тесты создания заказов")
public class OrderCreationTest extends BaseApiTest {

    // Реальные ID ингредиентов из ответа API
    private static final String BUN_ID = "61c0c5a71d1f82001bdaaa6d"; // Флюоресцентная булка R2-D3
    private static final String MEAT_ID = "61c0c5a71d1f82001bdaaa6f"; // Мясо бессмертных моллюсков
    private static final String SAUCE_ID = "61c0c5a71d1f82001bdaaa72"; // Соус Spicy-X

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        // Создаем пользователя и получаем токен
        String email = generateUniqueEmail();
        String token = registerTestUser(email, "password123", "Test User");

        // Создаем заказ с авторизацией и реальными ингредиентами
        String requestBody = String.format(
                "{\"ingredients\": [\"%s\", \"%s\", \"%s\"]}",
                BUN_ID, MEAT_ID, SAUCE_ID
        );

        Response response = postWithAuth("/orders", requestBody, token);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        String requestBody = String.format(
                "{\"ingredients\": [\"%s\", \"%s\", \"%s\"]}",
                BUN_ID, MEAT_ID, SAUCE_ID
        );

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void testCreateOrderWithIngredients() {
        String requestBody = String.format(
                "{\"ingredients\": [\"%s\", \"%s\", \"%s\"]}",
                BUN_ID, MEAT_ID, SAUCE_ID
        );

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        String requestBody = "{\"ingredients\": []}";

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        String requestBody = "{\"ingredients\": [\"invalid_hash_123\", \"another_invalid\"]}";

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(500)
                .body(notNullValue());
    }

    @Test
    @DisplayName("Создание заказа только с булкой")
    public void testCreateOrderWithBunOnly() {
        String requestBody = String.format(
                "{\"ingredients\": [\"%s\"]}",
                BUN_ID
        );

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа только с начинкой")
    public void testCreateOrderWithFillingOnly() {
        String requestBody = String.format(
                "{\"ingredients\": [\"%s\"]}",
                MEAT_ID
        );

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }
}