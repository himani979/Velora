package com.velora.backend.dto;

import java.time.LocalDate;

public class IncomeResponse {

    private Integer id;
    private String source;
    private Double amount;
    private LocalDate incomeDate;
    private String note;

    public IncomeResponse() {
    }

    public IncomeResponse(
            Integer id,
            String source,
            Double amount,
            LocalDate incomeDate,
            String note) {

        this.id = id;
        this.source = source;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}