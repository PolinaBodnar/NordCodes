package config;

public class TestConfig {

    public static final String BASE_URL = "http://localhost:8080";
    public static final String MOCK_BASE_URL = "http://localhost:8080";

    public static final String TEST_USERNAME = "admin";
    public static final String TEST_PASSWORD = "password";
    public static final String STATIC_API_KEY = "qazWSXedc";

    public static final int TOKEN_EXPIRATION_MINUTES = 15;

    /**
     * Возвращает базовый URL, который должен использовать API сервис.
     * Для мок-тестов используем MOCK_BASE_URL.
     */
    public static String getBaseUrl() {
        return MOCK_BASE_URL;   // ← WireMock по умолчанию
    }
}