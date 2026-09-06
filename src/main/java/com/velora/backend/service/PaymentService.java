package com.velora.backend.service;

import com.velora.backend.entity.Payment;
import com.velora.backend.entity.Subscription;
import com.velora.backend.entity.User;
import com.velora.backend.repository.PaymentRepository;
import com.velora.backend.repository.SubscriptionRepository;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    public Payment simulateAutoPayment(Integer subscriptionId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        Subscription subscription =
                subscriptionRepository.findById(subscriptionId).orElse(null);

        if (subscription == null) {
            return null;
        }

        if (!subscription.getUser().getId().equals(user.getId())) {
            return null;
        }

        if (!Boolean.TRUE.equals(subscription.getAutoPay())) {
            return null;
        }

        Payment payment = new Payment();

        payment.setAmount(subscription.getAmount());
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus("SUCCESS");
        payment.setPaymentMethod("AUTO_PAY");
        payment.setSubscription(subscription);
        payment.setUser(user);


        Payment savedPayment = paymentRepository.save(payment);

        if (subscription.getBillingCycle().equalsIgnoreCase("MONTHLY")) {

            subscription.setNextPaymentDate(
                    subscription.getNextPaymentDate().plusMonths(1)
            );

        } else if (subscription.getBillingCycle().equalsIgnoreCase("YEARLY")) {

            subscription.setNextPaymentDate(
                    subscription.getNextPaymentDate().plusYears(1)
            );
        }

        subscriptionRepository.save(subscription);

        return savedPayment;
    }

    public List<Payment> getPaymentHistory() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        return paymentRepository.findByUser(user);
    }
}