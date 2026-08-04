package com.velora.backend.repository;

import com.velora.backend.entity.Budget;
import com.velora.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    List<Budget> findByUser(User user);
}