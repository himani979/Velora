package com.velora.backend.controller;

import com.velora.backend.entity.Payment;
import com.velora.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.velora.backend.dto.PaymentResponse;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/simulate/{subscriptionId}")
    public PaymentResponse simulateAutoPayment(
            @PathVariable Integer subscriptionId) {

        Payment payment =
                paymentService.simulateAutoPayment(subscriptionId);

        if (payment == null) {
            return null;
        }

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getSubscription().getId()
        );
    }

    @GetMapping
    public List<PaymentResponse> getPaymentHistory() {

        List<Payment> payments =
                paymentService.getPaymentHistory();

        return payments.stream()
                .map(payment -> new PaymentResponse(
                        payment.getId(),
                        payment.getAmount(),
                        payment.getPaymentDate(),
                        payment.getStatus(),
                        payment.getPaymentMethod(),
                        payment.getSubscription().getId()
                ))
                .toList();
    }
}