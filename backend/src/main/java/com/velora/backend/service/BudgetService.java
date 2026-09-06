package com.velora.backend.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.velora.backend.dto.BudgetRequest;
import com.velora.backend.dto.BudgetResponse;
import com.velora.backend.entity.Budget;
import com.velora.backend.entity.Expense;
import com.velora.backend.entity.User;
import com.velora.backend.repository.BudgetRepository;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.repository.UserRepository;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;


    // =====================================================
    // GET CURRENT LOGGED-IN USER
    // =====================================================

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }


    // =====================================================
    // SAVE BUDGET
    // =====================================================

    public Budget saveBudget(BudgetRequest request) {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        // Check if same category budget already exists
        Optional<Budget> existingBudget =
                budgetRepository.findByUserAndCategoryIgnoreCaseAndMonthAndYear(
                        user,
                        request.getCategory(),
                        request.getMonth(),
                        request.getYear()
                );

        // If already exists, don't create duplicate
        if (existingBudget.isPresent()) {
            return null;
        }

        Budget budget = new Budget();

        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setUser(user);

        return budgetRepository.save(budget);
    }

    // =====================================================
    // GET ALL BUDGETS
    // =====================================================

    public List<Budget> getAllBudgets() {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        return budgetRepository.findByUser(user);
    }


    // =====================================================
    // GET BUDGETS FOR SELECTED MONTH
    // =====================================================

    public List<BudgetResponse> getAllBudgetResponses(
            int month,
            int year) {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        List<Budget> budgets =
                budgetRepository.findByUserAndMonthAndYear(
                        user,
                        month,
                        year
                );

        return budgets.stream()
                .map(budget ->
                        createBudgetResponse(
                                budget,
                                month,
                                year
                        )
                )
                .toList();
    }


    // =====================================================
    // CREATE BUDGET RESPONSE
    // =====================================================

    private BudgetResponse createBudgetResponse(
            Budget budget,
            int month,
            int year) {

        User user = budget.getUser();

        // Selected month
        YearMonth yearMonth =
                YearMonth.of(year, month);

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();


        // =================================================
        // GET EXPENSES OF SELECTED MONTH
        // =================================================

        List<Expense> expenses =
                expenseRepository.findByUserAndExpenseDateBetween(
                        user,
                        startDate,
                        endDate
                );


        // =================================================
        // CALCULATE SPENT FOR THIS CATEGORY
        // =================================================

        double spent = expenses.stream()
                .filter(expense ->
                        expense.getCategory() != null
                                &&
                                expense.getCategory()
                                        .equalsIgnoreCase(
                                                budget.getCategory()
                                        )
                )
                .mapToDouble(expense -> {

                    if (expense.getAmount() == null) {
                        return 0;
                    }

                    return expense.getAmount();
                })
                .sum();


        // =================================================
        // BUDGET AMOUNT
        // =================================================

        double budgetAmount =
                budget.getAmount() == null
                        ? 0
                        : budget.getAmount();


        // =================================================
        // REMAINING
        // =================================================

        double remaining =
                budgetAmount - spent;


        // =================================================
        // PROGRESS
        // =================================================

        double progress = 0;

        if (budgetAmount > 0) {

            progress =
                    (spent / budgetAmount) * 100;
        }


        // Never show progress above 100%
        if (progress > 100) {
            progress = 100;
        }


        // Never show negative progress
        if (progress < 0) {
            progress = 0;
        }


        // =================================================
        // RESPONSE
        // =================================================

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory(),
                budget.getAmount(),
                budget.getCreatedAt(),
                spent,
                remaining,
                progress
        );
    }


    // =====================================================
    // UPDATE BUDGET
    // =====================================================

    public Budget updateBudget(
            Integer id,
            BudgetRequest request) {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        Budget budget =
                budgetRepository
                        .findById(id)
                        .orElse(null);

        if (budget == null) {
            return null;
        }

        // Check ownership
        if (!budget.getUser()
                .getId()
                .equals(user.getId())) {

            return null;
        }

        // Update budget details
        budget.setCategory(
                request.getCategory()
        );

        budget.setAmount(
                request.getAmount()
        );

        // IMPORTANT:
        // Update month and year also
        budget.setMonth(
                request.getMonth()
        );

        budget.setYear(
                request.getYear()
        );

        return budgetRepository.save(budget);
    }


    // =====================================================
    // DELETE BUDGET
    // =====================================================

    public String deleteBudget(Integer id) {

        User user = getCurrentUser();

        if (user == null) {
            return "User not found";
        }


        Budget budget =
                budgetRepository
                        .findById(id)
                        .orElse(null);

        if (budget == null) {
            return "Budget not found";
        }


        // Budget must belong to logged-in user

        if (!budget.getUser()
                .getId()
                .equals(user.getId())) {

            return "You cannot delete this budget";
        }


        budgetRepository.delete(budget);

        return "Budget deleted successfully";
    }
}