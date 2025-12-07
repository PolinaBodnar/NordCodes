package service;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import config.TestConfig;

import static io.restassured.RestAssured.given;

public class ApiService {
    private final String baseUrl;

    public ApiService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Response performAction(String token, String action) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.URLENC)
                .header("X-Api-Key", TestConfig.STATIC_API_KEY)
                .formParam("token", token)
                .formParam("action", action)
                .when()
                .post("/endpoint");
    }

    // Метод для выполнения LOGIN действия
    public Response loginUser(String token) {
        return performAction(token, "LOGIN");
    }

    // Метод для выполнения ACTION
    public Response doAction(String token) {
        return performAction(token, "ACTION");
    }

    // Метод для завершения сессии
    public Response logout(String token) {
        return performAction(token, "LOGOUT");
    }
}