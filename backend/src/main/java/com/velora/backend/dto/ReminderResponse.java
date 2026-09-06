package com.velora.backend.dto;

import java.time.LocalDate;

public class ReminderResponse {

    private String serviceName;
    private String message;
    private Double amount;
    private LocalDate paymentDate;

    public ReminderResponse() {
    }

    public ReminderResponse(
            String serviceName,
            String message,
            Double amount,
            LocalDate paymentDate) {

        this.serviceName = serviceName;
        this.message = message;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}