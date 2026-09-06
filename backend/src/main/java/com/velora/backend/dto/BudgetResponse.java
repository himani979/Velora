package com.velora.backend.dto;

import java.time.LocalDateTime;

public class BudgetResponse {

    private Integer id;
    private String category;
    private Double amount;
    private LocalDateTime createdAt;

    private Double spent;
    private Double remaining;
    private Double progress;

    public BudgetResponse() {
    }

    public BudgetResponse(
            Integer id,
            String category,
            Double amount,
            LocalDateTime createdAt,
            Double spent,
            Double remaining,
            Double progress) {

        this.id = id;
        this.category = category;
        this.amount = amount;
        this.createdAt = createdAt;
        this.spent = spent;
        this.remaining = remaining;
        this.progress = progress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getSpent() {
        return spent;
    }

    public void setSpent(Double spent) {
        this.spent = spent;
    }

    public Double getRemaining() {
        return remaining;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }
}