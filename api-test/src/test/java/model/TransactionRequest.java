package model;

public class TransactionRequest {
    private String action;
    private Double amount;

    public TransactionRequest() {
        // Конструктор по умолчанию для десериализации
    }

    public TransactionRequest(String action) {
        this.action = action;
    }

    public TransactionRequest(String action, Double amount) {
        this.action = action;
        this.amount = amount;
    }

    // Геттеры и сеттеры
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}