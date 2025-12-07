import io.qameta.allure.*;
import io.restassured.response.Response;
import model.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Управление сессией")
@Feature("Завершение сессии пользователя")
public class SessionManagementTests extends BaseTest {

    private String validToken;

    @BeforeEach
    public void setupValidToken() {
        validToken = generateValidToken();

        // Настраиваем мок для успешной авторизации
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/auth"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)));

        // Настраиваем успешный ответ для LOGIN
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.containing("action=LOGIN"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"OK\",\"token\":\"" + validToken + "\"}")));

        // Выполняем авторизацию
        apiService.loginUser(validToken);
    }

    @Test
    @Story("Завершение сессии")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Успешное завершение сессии пользователя")
    public void testSuccessfulLogout() {
        // Настраиваем успешный ответ для LOGOUT
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.containing("action=LOGOUT"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"OK\"}")));

        // Act
        Response response = apiService.logout(validToken);

        // Assert
        assertEquals(200, response.getStatusCode(), "Код ответа должен быть 200");
    }

    @Test
    @Story("Завершение сессии")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Ошибка при использовании токена после завершения сессии")
    public void testTokenInvalidAfterLogout() {
        // Настраиваем успешный ответ для LOGOUT
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.containing("action=LOGOUT"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"OK\"}")));

        // Настраиваем ошибку для ACTION после LOGOUT
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.containing("action=ACTION"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"ERROR\",\"message\":\"invalid token\"}")));

        // Act — logout
        apiService.logout(validToken);

        // Act — повторная попытка действия
        Response response = apiService.doAction(validToken);

        // Assert
        assertEquals(200, response.getStatusCode(), "Код ответа должен быть 200");

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertEquals("ERROR", apiResponse.getResult(), "Результат должен быть ERROR");
        assertEquals("invalid token", apiResponse.getMessage(), "Сообщение должно быть 'invalid token'");
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