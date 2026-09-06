package com.velora.backend.dto;

import java.time.LocalDate;

public class PaymentResponse {

    private Integer id;
    private Double amount;
    private LocalDate paymentDate;
    private String status;
    private String paymentMethod;
    private Integer subscriptionId;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Integer id,
            Double amount,
            LocalDate paymentDate,
            String status,
            String paymentMethod,
            Integer subscriptionId) {

        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.subscriptionId = subscriptionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}