package com.velora.backend.repository;

import com.velora.backend.entity.Payment;
import com.velora.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByUser(User user);

    List<Payment> findBySubscriptionId(Integer subscriptionId);
}