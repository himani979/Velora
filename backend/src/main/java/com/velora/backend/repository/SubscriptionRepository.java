package com.velora.backend.repository;

import com.velora.backend.entity.Subscription;
import com.velora.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    List<Subscription> findByUser(User user);

    List<Subscription> findByUserAndNextPaymentDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
}