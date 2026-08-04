package com.velora.backend.dto;

public class BudgetRequest {

    private String category;
    private Double amount;

    // Getters

    public String getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }

    // Setters

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}