package com.velora.backend.dto;

import java.time.LocalDate;

public class ExpenseResponse {

    private Integer id;
    private String title;
    private Double amount;
    private String category;
    private LocalDate expenseDate;
    private String note;

    public ExpenseResponse() {
    }

    public ExpenseResponse(
            Integer id,
            String title,
            Double amount,
            String category,
            LocalDate expenseDate,
            String note) {

        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}