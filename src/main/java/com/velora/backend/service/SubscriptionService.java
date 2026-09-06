package com.velora.backend.service;

import com.velora.backend.dto.SubscriptionRequest;
import com.velora.backend.entity.Subscription;
import com.velora.backend.entity.User;
import com.velora.backend.repository.SubscriptionRepository;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    public Subscription saveSubscription(SubscriptionRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        Subscription subscription = new Subscription();

        subscription.setServiceName(request.getServiceName());
        subscription.setPlan(request.getPlan());
        subscription.setAmount(request.getAmount());
        subscription.setBillingCycle(request.getBillingCycle());
        subscription.setNextPaymentDate(request.getNextPaymentDate());
        subscription.setAutoPay(request.getAutoPay());
        subscription.setStatus(request.getStatus());
        subscription.setReminderDaysBefore(request.getReminderDaysBefore());
        subscription.setUser(user);

        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getAllSubscriptions() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        return subscriptionRepository.findByUser(user);
    }

    public List<Subscription> getUpcomingSubscriptions() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        LocalDate today = LocalDate.now();
        LocalDate next7Days = today.plusDays(7);

        return subscriptionRepository
                .findByUserAndNextPaymentDateBetween(
                        user,
                        today,
                        next7Days
                );
    }


    public Subscription updateSubscription(
            Integer id,
            SubscriptionRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        Subscription subscription =
                subscriptionRepository.findById(id).orElse(null);

        if (subscription == null) {
            return null;
        }

        if (!subscription.getUser().getId().equals(user.getId())) {
            return null;
        }

        subscription.setServiceName(request.getServiceName());
        subscription.setPlan(request.getPlan());
        subscription.setAmount(request.getAmount());
        subscription.setBillingCycle(request.getBillingCycle());
        subscription.setNextPaymentDate(request.getNextPaymentDate());
        subscription.setAutoPay(request.getAutoPay());
        subscription.setStatus(request.getStatus());
        subscription.setReminderDaysBefore(request.getReminderDaysBefore());

        return subscriptionRepository.save(subscription);
    }

    public String deleteSubscription(Integer id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "User not found";
        }

        Subscription subscription =
                subscriptionRepository.findById(id).orElse(null);

        if (subscription == null) {
            return "Subscription not found";
        }

        if (!subscription.getUser().getId().equals(user.getId())) {
            return "You cannot delete this subscription";
        }

        subscriptionRepository.delete(subscription);

        return "Subscription deleted successfully";
    }
    public Subscription toggleAutoPay(Integer id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        Subscription subscription =
                subscriptionRepository.findById(id).orElse(null);

        if (subscription == null) {
            return null;
        }

        if (!subscription.getUser().getId().equals(user.getId())) {
            return null;
        }

        subscription.setAutoPay(!Boolean.TRUE.equals(subscription.getAutoPay()));

        return subscriptionRepository.save(subscription);
    }
    public List<Subscription> getPaymentReminders() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        LocalDate today = LocalDate.now();

        List<Subscription> subscriptions =
                subscriptionRepository.findByUser(user);

        List<Subscription> reminders = new ArrayList<>();

        for (Subscription subscription : subscriptions) {

            if (!subscription.getAutoPay()) {
                continue;
            }

            LocalDate reminderDate =
                    subscription.getNextPaymentDate()
                            .minusDays(subscription.getReminderDaysBefore());

            if (!today.isBefore(reminderDate)
                    && !today.isAfter(subscription.getNextPaymentDate())) {

                reminders.add(subscription);
            }
        }

        return reminders;
    }
}