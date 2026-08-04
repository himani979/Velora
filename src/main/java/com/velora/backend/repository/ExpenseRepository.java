package com.velora.backend.repository;

import com.velora.backend.entity.Expense;
import com.velora.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByUser(User user);
    Long countByUser(User user);

    List<Expense> findByUserOrderByAmountDesc(User user);
    List<Expense> findByUserOrderByCategory(User user);
}