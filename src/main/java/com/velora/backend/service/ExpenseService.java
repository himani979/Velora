package com.velora.backend.service;

import com.velora.backend.entity.User;
import com.velora.backend.repository.UserRepository;
import com.velora.backend.dto.ExpenseRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import com.velora.backend.entity.Expense;
import com.velora.backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;



    public List<Expense> getAllExpenses() {

        // Get logged-in user's email from JWT
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find user by email
        User user = userRepository.findByEmail(email).orElse(null);

        // Return only this user's expenses
        return expenseRepository.findByUser(user);
    }




    public Expense saveExpense(ExpenseRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setNote(request.getNote());

        expense.setUser(user);

        return expenseRepository.save(expense);
    }
    public Expense updateExpense(Integer id, ExpenseRequest request) {

        // Get logged-in user's email from JWT
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find logged-in user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        // Find expense by ID
        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null) {
            return null;
        }

        // Check if this expense belongs to the logged-in user
        if (!expense.getUser().getId().equals(user.getId())) {
            return null;
        }

        // Update fields
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setNote(request.getNote());

        return expenseRepository.save(expense);
    }
    public String deleteExpense(Integer id) {

        // Get logged-in user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find logged-in user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "User not found";
        }

        // Find expense
        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null) {
            return "Expense not found";
        }

        // Check ownership
        if (!expense.getUser().getId().equals(user.getId())) {
            return "You cannot delete this expense";
        }

        expenseRepository.delete(expense);

        return "Expense deleted successfully";
    }
    public List<Object[]> getCategorySummary() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        return expenseRepository.getCategorySummary(user);
    }
    public Expense getExpenseById(Integer id) {

        // Logged-in user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find logged-in user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        // Find expense
        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null) {
            return null;
        }

        // Check ownership
        if (!expense.getUser().getId().equals(user.getId())) {
            return null;
        }

        return expense;
    }
    public List<Expense> searchExpenseByTitle(String title) {

        // Logged-in user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find logged-in user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        return expenseRepository.findByUserAndTitleContainingIgnoreCase(user, title);
    }
    public List<Expense> getExpensesByCategory(String category) {

        // Logged-in user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find logged-in user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        return expenseRepository.findByUserAndCategoryIgnoreCase(user, category);
    }
    public List<Expense> getExpensesByDateRange(LocalDate start, LocalDate end) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return List.of();
        }

        return expenseRepository.findByUserAndExpenseDateBetween(user, start, end);
    }
    public Double getTotalExpense() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return 0.0;
        }

        return expenseRepository.getTotalExpenseByUser(user);
    }
    public Expense getHighestExpense() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        return expenseRepository.findTopByUserOrderByAmountDesc(user);
    }
    public Double getMonthlyExpense() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

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