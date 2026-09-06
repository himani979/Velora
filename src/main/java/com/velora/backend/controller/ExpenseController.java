package com.velora.backend.controller;

import com.velora.backend.dto.ExpenseRequest;
import com.velora.backend.entity.Expense;
import com.velora.backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.velora.backend.dto.ExpenseResponse;
import java.time.LocalDate;
import java.util.List;

import com.velora.backend.dto.CategorySummaryResponse;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ExpenseResponse addExpense(
            @Valid @RequestBody ExpenseRequest request) {

        Expense expense = expenseService.saveExpense(request);

        if (expense == null) {
            return null;
        }

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate(),
                expense.getNote()
        );
    }

    @GetMapping
    public List<ExpenseResponse> getAllExpenses() {

        List<Expense> expenses = expenseService.getAllExpenses();

        return expenses.stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getTitle(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getExpenseDate(),
                        expense.getNote()
                ))
                .toList();
    }

    @GetMapping("/search")
    public List<ExpenseResponse> searchExpense(
            @RequestParam String title) {

        List<Expense> expenses =
                expenseService.searchExpenseByTitle(title);

        return expenses.stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getTitle(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getExpenseDate(),
                        expense.getNote()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpenseById(
            @PathVariable Integer id) {

        Expense expense = expenseService.getExpenseById(id);

        if (expense == null) {
            return null;
        }

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate(),
                expense.getNote()
        );
    }

    @GetMapping("/category/{category}")
    public List<ExpenseResponse> getExpensesByCategory(
            @PathVariable String category) {

        List<Expense> expenses =
                expenseService.getExpensesByCategory(category);

        return expenses.stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getTitle(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getExpenseDate(),
                        expense.getNote()
                ))
                .toList();
    }

    @GetMapping("/date")
    public List<ExpenseResponse> getExpensesByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        List<Expense> expenses =
                expenseService.getExpensesByDateRange(start, end);

        return expenses.stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getTitle(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getExpenseDate(),
                        expense.getNote()
                ))
                .toList();
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(
            @PathVariable Integer id,
            @Valid @RequestBody ExpenseRequest request) {

        Expense expense =
                expenseService.updateExpense(id, request);

        if (expense == null) {
            return null;
        }

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate(),
                expense.getNote()
        );
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(
            @PathVariable Integer id) {

        return expenseService.deleteExpense(id);
    }

    @GetMapping("/total")
    public Double getTotalExpense() {
        return expenseService.getTotalExpense();
    }

    @GetMapping("/highest")
    public ExpenseResponse getHighestExpense() {

        Expense expense = expenseService.getHighestExpense();

        if (expense == null) {
            return null;
        }

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate(),
                expense.getNote()
        );
    }

    @GetMapping("/monthly")
    public Double getMonthlyExpense() {
        return expenseService.getMonthlyExpense();
    }

    @GetMapping("/category-summary")
    public List<CategorySummaryResponse> getCategorySummary() {
        return expenseService.getCategorySummary();
    }
}
