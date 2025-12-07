import io.qameta.allure.*;
import io.restassured.response.Response;
import model.ApiResponse;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Транзакции")
@Feature("Операции с транзакциями")
public class TransactionTests extends BaseTest {

    private String validToken;

    @BeforeAll
    public void setupValidToken() {
        // Убедимся, что wireMockServer инициализирован
        if (wireMockServer == null) {
            setUp(); // Вызов метода из BaseTest
        }

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
    @Story("Успешная транзакция")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Успешное выполнение действия")
    public void testSuccessfulAction() {
        // Настраиваем успешный ответ для ACTION
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.containing("action=ACTION"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"OK\"}")));

        // Act
        Response response = apiService.doAction(validToken);

        // Assert
        assertEquals(200, response.getStatusCode(), "Код ответа должен быть 200");

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertEquals("OK", apiResponse.getResult(), "Результат должен быть OK");
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