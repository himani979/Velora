package com.velora.backend.controller;

import com.velora.backend.dto.SubscriptionRequest;
import com.velora.backend.entity.Subscription;
import com.velora.backend.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.velora.backend.dto.SubscriptionResponse;
import com.velora.backend.dto.ReminderResponse;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public SubscriptionResponse saveSubscription(
            @Valid @RequestBody SubscriptionRequest request) {

        Subscription subscription =
                subscriptionService.saveSubscription(request);

        if (subscription == null) {
            return null;
        }

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getServiceName(),
                subscription.getPlan(),
                subscription.getAmount(),
                subscription.getBillingCycle(),
                subscription.getNextPaymentDate(),
                subscription.getAutoPay(),
                subscription.getStatus(),
                subscription.getReminderDaysBefore()
        );
    }

    @GetMapping
    public List<SubscriptionResponse> getAllSubscriptions() {

        List<Subscription> subscriptions =
                subscriptionService.getAllSubscriptions();

        return subscriptions.stream()
                .map(subscription -> new SubscriptionResponse(
                        subscription.getId(),
                        subscription.getServiceName(),
                        subscription.getPlan(),
                        subscription.getAmount(),
                        subscription.getBillingCycle(),
                        subscription.getNextPaymentDate(),
                        subscription.getAutoPay(),
                        subscription.getStatus(),
                        subscription.getReminderDaysBefore()
                ))
                .toList();
    }

    @GetMapping("/upcoming")
    public List<SubscriptionResponse> getUpcomingSubscriptions() {

        List<Subscription> subscriptions =
                subscriptionService.getUpcomingSubscriptions();

        return subscriptions.stream()
                .map(subscription -> new SubscriptionResponse(
                        subscription.getId(),
                        subscription.getServiceName(),
                        subscription.getPlan(),
                        subscription.getAmount(),
                        subscription.getBillingCycle(),
                        subscription.getNextPaymentDate(),
                        subscription.getAutoPay(),
                        subscription.getStatus(),
                        subscription.getReminderDaysBefore()
                ))
                .toList();
    }

    @PutMapping("/{id}")
    public SubscriptionResponse updateSubscription(
            @PathVariable Integer id,
            @Valid @RequestBody SubscriptionRequest request) {

        Subscription subscription =
                subscriptionService.updateSubscription(id, request);

        if (subscription == null) {
            return null;
        }

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getServiceName(),
                subscription.getPlan(),
                subscription.getAmount(),
                subscription.getBillingCycle(),
                subscription.getNextPaymentDate(),
                subscription.getAutoPay(),
                subscription.getStatus(),
                subscription.getReminderDaysBefore()
        );
    }

    @DeleteMapping("/{id}")
    public String deleteSubscription(
            @PathVariable Integer id) {

        return subscriptionService.deleteSubscription(id);
    }

    @PutMapping("/{id}/autopay")
    public SubscriptionResponse toggleAutoPay(
            @PathVariable Integer id) {

        Subscription subscription =
                subscriptionService.toggleAutoPay(id);

        if (subscription == null) {
            return null;
        }

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getServiceName(),
                subscription.getPlan(),
                subscription.getAmount(),
                subscription.getBillingCycle(),
                subscription.getNextPaymentDate(),
                subscription.getAutoPay(),
                subscription.getStatus(),
                subscription.getReminderDaysBefore()
        );
    }

    @GetMapping("/reminders")
    public List<ReminderResponse> getPaymentReminders() {

        List<Subscription> subscriptions =
                subscriptionService.getPaymentReminders();

        return subscriptions.stream()
                .map(subscription -> {

                    long daysLeft =
                            java.time.temporal.ChronoUnit.DAYS.between(
                                    java.time.LocalDate.now(),
                                    subscription.getNextPaymentDate()
                            );

                    String message;

                    if (daysLeft == 0) {
                        message = subscription.getServiceName()
                                + " payment is due today";

                    } else if (daysLeft == 1) {
                        message = subscription.getServiceName()
                                + " payment is due tomorrow";

                    } else {
                        message = subscription.getServiceName()
                                + " payment is due in "
                                + daysLeft
                                + " days";
                    }

                    return new ReminderResponse(
                            subscription.getServiceName(),
                            message,
                            subscription.getAmount(),
                            subscription.getNextPaymentDate()
                    );
                })
                .toList();
    }
}