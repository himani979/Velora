package com.velora.backend.dto;

import java.time.LocalDate;

public class SubscriptionResponse {

    private Integer id;
    private String serviceName;
    private String plan;
    private Double amount;
    private String billingCycle;
    private LocalDate nextPaymentDate;
    private Boolean autoPay;
    private String status;
    private Integer reminderDaysBefore;

    public SubscriptionResponse() {
    }

    public SubscriptionResponse(
            Integer id,
            String serviceName,
            String plan,
            Double amount,
            String billingCycle,
            LocalDate nextPaymentDate,
            Boolean autoPay,
            String status,
            Integer reminderDaysBefore) {

        this.id = id;
        this.serviceName = serviceName;
        this.plan = plan;
        this.amount = amount;
        this.billingCycle = billingCycle;
        this.nextPaymentDate = nextPaymentDate;
        this.autoPay = autoPay;
        this.status = status;
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public Boolean getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(Integer reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }
}