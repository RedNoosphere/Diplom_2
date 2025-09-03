package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

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
    @Description("Проверка успешного создания заказа авторизованным пользователем с валидными ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        // Создаем пользователя и получаем токен
        String email = generateUniqueEmail();
        String token = registerTestUser(email, "password123", "Test User");

        // Создаем заказ с авторизацией и реальными ингредиентами
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{BUN_ID, MEAT_ID, SAUCE_ID});

        Response response = postWithAuth("/orders", requestBody, token);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа неавторизованным пользователем с валидными ингредиентами")
    public void testCreateOrderWithoutAuth() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{BUN_ID, MEAT_ID, SAUCE_ID});

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Проверка создания заказа с валидным набором ингредиентов")
    public void testCreateOrderWithIngredients() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{BUN_ID, MEAT_ID, SAUCE_ID});

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка обработки ошибки при создании заказа без указания ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{});

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка обработки ошибки при создании заказа с невалидными хешами ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{"invalid_hash_123", "another_invalid"});

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .body(notNullValue());
    }

    @Test
    @DisplayName("Создание заказа только с булкой")
    @Description("Проверка создания заказа только с булкой без других ингредиентов")
    public void testCreateOrderWithBunOnly() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{BUN_ID});

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа только с начинкой")
    @Description("Проверка создания заказа только с начинкой без булки")
    public void testCreateOrderWithFillingOnly() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", new String[]{MEAT_ID});

        Response response = post("/orders", requestBody);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }
}