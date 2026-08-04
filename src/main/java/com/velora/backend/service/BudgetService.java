package com.velora.backend.service;

import java.util.List;
import com.velora.backend.dto.BudgetRequest;
import com.velora.backend.entity.Budget;
import com.velora.backend.entity.User;
import com.velora.backend.repository.BudgetRepository;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    public Budget saveBudget(BudgetRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        Budget budget = new Budget();

        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setUser(user);

        return budgetRepository.save(budget);
    }
    public List<Budget> getAllBudgets() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        return budgetRepository.findByUser(user);
    }
    public Budget updateBudget(Integer id, BudgetRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        Budget budget = budgetRepository.findById(id).orElse(null);

        if (budget == null) {
            return null;
        }

        if (!budget.getUser().getId().equals(user.getId())) {
            return null;
        }

        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());

        return budgetRepository.save(budget);
    }
    public String deleteBudget(Integer id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "User not found";
        }

        Budget budget = budgetRepository.findById(id).orElse(null);

        if (budget == null) {
            return "Budget not found";
        }

        if (!budget.getUser().getId().equals(user.getId())) {
            return "You cannot delete this budget";
        }

        budgetRepository.delete(budget);

        return "Budget deleted successfully";
    }
}