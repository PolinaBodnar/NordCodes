package model;

public class ApiResponse {
    private String result;
    private String token;
    private Double balance;
    private String message;

    // Конструктор по умолчанию (необходим для десериализации JSON)
    public ApiResponse() {
    }

    // Конструктор со всеми параметрами
    public ApiResponse(String result, String token, Double balance, String message) {
        this.result = result;
        this.token = token;
        this.balance = balance;
        this.message = message;
    }

    // Геттеры и сеттеры
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Метод toString для удобства отладки
    @Override
    public String toString() {
        return "ApiResponse{" +
                "result='" + result + '\'' +
                ", token='" + token + '\'' +
                ", balance=" + balance +
                ", message='" + message + '\'' +
                '}';
    }
}