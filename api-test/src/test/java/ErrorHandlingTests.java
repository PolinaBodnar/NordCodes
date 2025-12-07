import config.TestConfig;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Обработка ошибок")
@Feature("Обработка различных типов ошибок")
public class ErrorHandlingTests extends BaseTest {

    @Test
    @Story("Ошибка формата")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Ошибка при неверном формате Content-Type")
    public void testWrongContentType() {
        // Arrange
        String validToken = generateValidToken();

        // Настраиваем специфичный ответ для неверного Content-Type
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withHeader("Content-Type", com.github.tomakehurst.wiremock.client.WireMock.containing("text/plain"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"ERROR\",\"message\":\"Invalid content type\"}")));

        // Act
        Response response = given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.TEXT) // Намеренно неверный тип контента
                .header("X-Api-Key", TestConfig.STATIC_API_KEY)
                .formParam("token", validToken)
                .formParam("action", "ACTION")
                .when()
                .post("/endpoint");

        // Assert
        assertEquals(400, response.getStatusCode(), "Код ответа должен быть 400");
    }

    @Test
    @Story("Ошибка метода")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Ошибка при использовании неверного HTTP метода")
    public void testWrongHttpMethod() {
        // Arrange
        String validToken = generateValidToken();

        // Настраиваем специфичный ответ для GET запросов
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .get(com.github.tomakehurst.wiremock.client.WireMock.anyUrl())
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(405)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"ERROR\",\"message\":\"Method not allowed\"}")));

        // Act — отправляем GET вместо POST
        Response response = given()
                .baseUri("http://localhost:8080")
                .header("X-Api-Key", "qazWSXedc")
                .formParam("token", validToken)
                .formParam("action", "ACTION")
                .when()
                .get("/endpoint");

        // Assert
        assertEquals(405, response.getStatusCode(), "Код ответа должен быть 405 (Method Not Allowed)");
    }

    @Test
    @Story("Ошибка формата данных")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Ошибка при отсутствии обязательных параметров")
    public void testMissingRequiredParameters() {
        // Arrange
        String validToken = generateValidToken();

        // Настраиваем ответ для отсутствующих параметров
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.notMatching(".*action=.*"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"ERROR\",\"message\":\"Missing required parameters\"}")));

        // Act - отправляем запрос без action
        Response response = given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.URLENC)
                .header("X-Api-Key", TestConfig.STATIC_API_KEY)
                .formParam("token", validToken)
                // action отсутствует
                .when()
                .post("/endpoint");

        // Assert
        assertEquals(400, response.getStatusCode(), "Код ответа должен быть 400");
    }

    private String generateValidToken() {
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}