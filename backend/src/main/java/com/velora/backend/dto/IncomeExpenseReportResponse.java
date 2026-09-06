package com.velora.backend.dto;

public class IncomeExpenseReportResponse {

    private Double totalIncome;
    private Double totalExpense;
    private Double savings;

    public IncomeExpenseReportResponse() {
    }

    public IncomeExpenseReportResponse(Double totalIncome,
                                       Double totalExpense,
                                       Double savings) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.savings = savings;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getSavings() {
        return savings;
    }

    public void setSavings(Double savings) {
        this.savings = savings;
    }
}