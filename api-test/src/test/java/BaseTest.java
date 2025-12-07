import com.github.tomakehurst.wiremock.WireMockServer;
import config.TestConfig;
import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.ApiService;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class BaseTest {

    protected ApiService apiService;
    protected WireMockServer wireMockServer;

    @BeforeEach
    @Step("Подготовка тестового окружения")
    public void setUp() {
        // Запуск WireMock на порту 8080
        wireMockServer = new WireMockServer(options().port(8080));
        wireMockServer.start();
        wireMockServer.resetAll(); // сброс всех маппингов перед тестом

        // Инициализация API-сервиса
        apiService = new ApiService("http://localhost:8080");

        // Базовый стаб для endpoint
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock
                .post(com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/endpoint"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock
                        .aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"OK\"}")));
    }

    @AfterEach
    @Step("Очистка и завершение теста")
    public void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.resetAll(); // сброс после теста
            wireMockServer.stop();
        }
    }
}