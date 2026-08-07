package com.velora.backend.dto;

public class DashboardSummaryResponse {

    private Long totalExpenses;
    private Double totalAmount;
    private Double highestExpense;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(Long totalExpenses, Double totalAmount, Double highestExpense) {
        this.totalExpenses = totalExpenses;
        this.totalAmount = totalAmount;
        this.highestExpense = highestExpense;
    }

    public Long getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Long totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getHighestExpense() {
        return highestExpense;
    }

    public void setHighestExpense(Double highestExpense) {
        this.highestExpense = highestExpense;
    }
}