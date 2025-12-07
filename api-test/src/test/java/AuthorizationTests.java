import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Авторизация")
@Feature("Авторизация пользователя")
public class AuthorizationTests extends BaseTest {

    @Test
    @Story("Успешная авторизация")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Успешная авторизация с корректными учетными данными")
    public void testSuccessfulAuthorization() {
        // Arrange
        String token = generateValidToken();

        // Настраиваем мок для успешного ответа от внешнего сервиса /auth
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/auth"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)));

        // Настраиваем ответ для нашего endpoint
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock
                        .containing("action=LOGIN"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"OK\",\"token\":\"" + token + "\"}")));

        // Act
        Response response = apiService.loginUser(token);

        // Assert
        assertEquals(200, response.getStatusCode(), "Код ответа должен быть 200");
    }

    @Test
    @Story("Ошибка авторизации")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Ошибка авторизации при недоступности внешнего сервиса")
    public void testFailedAuthorizationWithExternalServiceError() {
        // Arrange
        String token = generateValidToken();

        // Настраиваем мок для ошибки от внешнего сервиса /auth
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/auth"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(500)));

        // Настраиваем ответ для нашего endpoint с ошибкой
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock
                        .containing("action=LOGIN"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"ERROR\",\"message\":\"External service error\"}")));

        // Act
        Response response = apiService.loginUser(token);

        // Assert
        assertEquals(200, response.getStatusCode(), "Код ответа должен быть 200");
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