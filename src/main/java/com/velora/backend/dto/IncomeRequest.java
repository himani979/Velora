package com.velora.backend.dto;

import java.time.LocalDate;

public class IncomeRequest {

    private String source;
    private Double amount;
    private LocalDate incomeDate;
    private String note;

    // Getters

    public String getSource() {
        return source;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public String getNote() {
        return note;
    }

    // Setters

    public void setSource(String source) {
        this.source = source;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
    }

    public void setNote(String note) {
        this.note = note;
    }
}