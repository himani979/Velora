
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
import com.velora.backend.util.CategoryUtils;

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


    // =========================================
    // GET CURRENT LOGGED-IN USER
    // =========================================

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }


    // =========================================
    // SAVE BUDGET
    // =========================================

    public Budget saveBudget(BudgetRequest request) {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        String category = CategoryUtils.normalize(
                request.getCategory()
        );

        // Check duplicate budgets, including old category names
        boolean budgetExists = budgetRepository
                .findByUserAndMonthAndYear(
                        user,
                        request.getMonth(),
                        request.getYear()
                )
                .stream()
                .anyMatch(existing ->
                        CategoryUtils.isSame(
                                existing.getCategory(),
                                category
                        )
                );

        if (budgetExists) {
            return null;
        }

        Budget budget = new Budget();

        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setUser(user);

        return budgetRepository.save(budget);
    }


    // =========================================
    // GET ALL BUDGETS
    // =========================================

    public List<Budget> getAllBudgets() {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        return budgetRepository.findByUser(user);
    }


    // =========================================
    // GET BUDGETS FOR SELECTED MONTH
    // =========================================

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


    // =========================================
    // CREATE BUDGET RESPONSE
    // =========================================

    private BudgetResponse createBudgetResponse(
            Budget budget,
            int month,
            int year) {

        User user = budget.getUser();

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Get expenses for selected month
        List<Expense> expenses =
                expenseRepository.findByUserAndExpenseDateBetween(
                        user,
                        startDate,
                        endDate
                );

        // Calculate spending using normalized categories
        double spent = expenses.stream()
                .filter(expense ->
                        CategoryUtils.isSame(
                                expense.getCategory(),
                                budget.getCategory()
                        )
                )
                .mapToDouble(expense ->
                        expense.getAmount() == null
                                ? 0
                                : expense.getAmount()
                )
                .sum();

        double budgetAmount =
                budget.getAmount() == null
                        ? 0
                        : budget.getAmount();

        double remaining = budgetAmount - spent;

        double progress = 0;

        if (budgetAmount > 0) {
            progress = (spent / budgetAmount) * 100;
        }

        // Keep progress between 0 and 100
        progress = Math.max(0, Math.min(progress, 100));

        return new BudgetResponse(
                budget.getId(),
                CategoryUtils.normalize(budget.getCategory()),
                budget.getAmount(),
                budget.getCreatedAt(),
                spent,
                remaining,
                progress
        );
    }


    // =========================================
    // UPDATE BUDGET
    // =========================================

    public Budget updateBudget(
            Integer id,
            BudgetRequest request) {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        Budget budget = budgetRepository
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

        String category = CategoryUtils.normalize(
                request.getCategory()
        );

        // Check if another budget already uses this category
        // in the requested month and year
        boolean duplicateExists = budgetRepository
                .findByUserAndMonthAndYear(
                        user,
                        request.getMonth(),
                        request.getYear()
                )
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(budget.getId())
                                &&
                                CategoryUtils.isSame(
                                        existing.getCategory(),
                                        category
                                )
                );

        if (duplicateExists) {
            return null;
        }

        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        return budgetRepository.save(budget);
    }


    // =========================================
    // DELETE BUDGET
    // =========================================

    public String deleteBudget(Integer id) {

        User user = getCurrentUser();

        if (user == null) {
            return "User not found";
        }

        Budget budget = budgetRepository
                .findById(id)
                .orElse(null);

        if (budget == null) {
            return "Budget not found";
        }

        // Check ownership
        if (!budget.getUser()
                .getId()
                .equals(user.getId())) {

            return "You cannot delete this budget";
        }

        budgetRepository.delete(budget);

        return "Budget deleted successfully";
    }
}
