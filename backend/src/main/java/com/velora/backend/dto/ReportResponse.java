package com.velora.backend.dto;

public class ReportResponse {

    private String label;
    private Double amount;

    public ReportResponse() {
    }

    public ReportResponse(String label, Double amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public Double getAmount() {
        return amount;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}