
package com.velora.backend.dto;

public class FutureLabRequest {

    private Integer months = 6;

    private Double monthlyIncome;
    private Double monthlyExpenses;

    private Double extraMonthlySavings = 0.0;
    private Double expenseIncreasePercent = 0.0;
    private Double cancelledSubscriptionAmount = 0.0;

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Double getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(Double monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public Double getExtraMonthlySavings() {
        return extraMonthlySavings;
    }

    public void setExtraMonthlySavings(Double extraMonthlySavings) {
        this.extraMonthlySavings = extraMonthlySavings;
    }

    public Double getExpenseIncreasePercent() {
        return expenseIncreasePercent;
    }

    public void setExpenseIncreasePercent(
            Double expenseIncreasePercent) {
        this.expenseIncreasePercent =
                expenseIncreasePercent;
    }

    public Double getCancelledSubscriptionAmount() {
        return cancelledSubscriptionAmount;
    }

    public void setCancelledSubscriptionAmount(
            Double cancelledSubscriptionAmount) {
        this.cancelledSubscriptionAmount =
                cancelledSubscriptionAmount;
    }
}
