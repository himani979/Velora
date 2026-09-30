
package com.velora.backend.service;

import com.velora.backend.entity.User;
import com.velora.backend.entity.Expense;
import com.velora.backend.repository.UserRepository;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.dto.ExpenseRequest;
import com.velora.backend.dto.CategorySummaryResponse;
import com.velora.backend.util.CategoryUtils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================
    // GET CURRENT USER
    // =========================

    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email).orElse(null);
    }

    // =========================
    // GET ALL EXPENSES
    // =========================

    public List<Expense> getAllExpenses() {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        return expenseRepository.findByUser(user);
    }

    // =========================
    // SAVE EXPENSE
    // =========================

    public Expense saveExpense(ExpenseRequest request) {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());

        // Standardize category before saving
        expense.setCategory(
                CategoryUtils.normalize(request.getCategory())
        );

        expense.setExpenseDate(request.getExpenseDate());
        expense.setNote(request.getNote());
        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    // =========================
    // UPDATE EXPENSE
    // =========================

    public Expense updateExpense(
            Integer id,
            ExpenseRequest request) {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        Expense expense = expenseRepository
                .findById(id)
                .orElse(null);

        if (expense == null) {
            return null;
        }

        // Check ownership
        if (!expense.getUser()
                .getId()
                .equals(user.getId())) {

            return null;
        }

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());

        // Standardize category before updating
        expense.setCategory(
                CategoryUtils.normalize(request.getCategory())
        );

        expense.setExpenseDate(request.getExpenseDate());
        expense.setNote(request.getNote());

        return expenseRepository.save(expense);
    }

    // =========================
    // DELETE EXPENSE
    // =========================

    public String deleteExpense(Integer id) {

        User user = getCurrentUser();

        if (user == null) {
            return "User not found";
        }

        Expense expense = expenseRepository
                .findById(id)
                .orElse(null);

        if (expense == null) {
            return "Expense not found";
        }

        if (!expense.getUser()
                .getId()
                .equals(user.getId())) {

            return "You cannot delete this expense";
        }

        expenseRepository.delete(expense);

        return "Expense deleted successfully";
    }

    // =========================
    // CATEGORY SUMMARY
    // =========================

    public List<CategorySummaryResponse> getCategorySummary() {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        // Combine equivalent categories
        Map<String, Double> categoryTotals =
                new LinkedHashMap<>();

        for (Expense expense : expenses) {

            String category = CategoryUtils.normalize(
                    expense.getCategory()
            );

            double amount = expense.getAmount() == null
                    ? 0
                    : expense.getAmount();

            categoryTotals.merge(
                    category,
                    amount,
                    Double::sum
            );
        }

        return categoryTotals.entrySet()
                .stream()
                .map(entry -> new CategorySummaryResponse(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

    // =========================
    // GET EXPENSE BY ID
    // =========================

    public Expense getExpenseById(Integer id) {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        Expense expense = expenseRepository
                .findById(id)
                .orElse(null);

        if (expense == null) {
            return null;
        }

        if (!expense.getUser()
                .getId()
                .equals(user.getId())) {

            return null;
        }

        return expense;
    }

    // =========================
    // SEARCH BY TITLE
    // =========================

    public List<Expense> searchExpenseByTitle(String title) {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        return expenseRepository
                .findByUserAndTitleContainingIgnoreCase(
                        user,
                        title
                );
    }

    // =========================
    // GET EXPENSES BY CATEGORY
    // =========================

    public List<Expense> getExpensesByCategory(String category) {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        return expenseRepository.findByUser(user)
                .stream()
                .filter(expense ->
                        CategoryUtils.isSame(
                                expense.getCategory(),
                                category
                        )
                )
                .toList();
    }

    // =========================
    // GET EXPENSES BY DATE RANGE
    // =========================

    public List<Expense> getExpensesByDateRange(
            LocalDate start,
            LocalDate end) {

        User user = getCurrentUser();

        if (user == null) {
            return List.of();
        }

        return expenseRepository
                .findByUserAndExpenseDateBetween(
                        user,
                        start,
                        end
                );
    }

    // =========================
    // TOTAL EXPENSE
    // =========================

    public Double getTotalExpense() {

        User user = getCurrentUser();

        if (user == null) {
            return 0.0;
        }

        return expenseRepository
                .getTotalExpenseByUser(user);
    }

    // =========================
    // HIGHEST EXPENSE
    // =========================

    public Expense getHighestExpense() {

        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        return expenseRepository
                .findTopByUserOrderByAmountDesc(user);
    }

    // =========================
    // MONTHLY EXPENSE
    // =========================

    public Double getMonthlyExpense() {

        User user = getCurrentUser();

        if (user == null) {
            return 0.0;
        }

        LocalDate today = LocalDate.now();

        return expenseRepository.getMonthlyExpense(
                user,
                today.getMonthValue(),
                today.getYear()
        );
    }
}
