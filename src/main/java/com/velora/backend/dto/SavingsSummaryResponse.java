package com.velora.backend.dto;

public class SavingsSummaryResponse {

    private Double totalIncome;
    private Double totalExpense;
    private Double totalBudget;
    private Double savings;
    private Double remainingBudget;
    private Double budgetUsagePercentage;

    private String budgetStatus;
    public SavingsSummaryResponse() {
    }

    public SavingsSummaryResponse(Double totalIncome,
                                  Double totalExpense,
                                  Double totalBudget,
                                  Double savings,
                                  Double remainingBudget,
                                  Double budgetUsagePercentage,
                                  String budgetStatus) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalBudget = totalBudget;
        this.savings = savings;
        this.remainingBudget = remainingBudget;
        this.budgetUsagePercentage = budgetUsagePercentage;
        this.budgetStatus = budgetStatus;
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

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Double getSavings() {
        return savings;
    }

    public void setSavings(Double savings) {
        this.savings = savings;
    }

    public Double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(Double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public Double getBudgetUsagePercentage() {
        return budgetUsagePercentage;
    }

    public void setBudgetUsagePercentage(Double budgetUsagePercentage) {
        this.budgetUsagePercentage = budgetUsagePercentage;
    }

    public String getBudgetStatus() {
        return budgetStatus;
    }

    public void setBudgetStatus(String budgetStatus) {
        this.budgetStatus = budgetStatus;
    }
}