package com.velora.backend.dto;

public class DashboardSummaryResponse {

    private Long totalExpenses;
    private Double totalAmount;
    private Double highestExpense;
    private Double monthlySubscriptionAmount;
    private Long activeSubscriptions;
    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(Long totalExpenses, Double totalAmount, Double highestExpense,Double monthlySubscriptionAmount,
                                    Long activeSubscriptions) {
        this.totalExpenses = totalExpenses;
        this.totalAmount = totalAmount;
        this.highestExpense = highestExpense;
        this.monthlySubscriptionAmount = monthlySubscriptionAmount;
        this.activeSubscriptions = activeSubscriptions;
    }
    public Double getMonthlySubscriptionAmount() {
        return monthlySubscriptionAmount;
    }

    public void setMonthlySubscriptionAmount(Double monthlySubscriptionAmount) {
        this.monthlySubscriptionAmount = monthlySubscriptionAmount;
    }

    public Long getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(Long activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
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