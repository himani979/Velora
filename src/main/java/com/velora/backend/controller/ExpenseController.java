package com.velora.backend.controller;

import com.velora.backend.dto.ExpenseRequest;
import com.velora.backend.entity.Expense;
import com.velora.backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public Expense addExpense(@RequestBody ExpenseRequest request) {
        return expenseService.saveExpense(request);
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }
    @GetMapping("/search")
    public List<Expense> searchExpense(@RequestParam String title) {
        return expenseService.searchExpenseByTitle(title);
    }
    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Integer id) {
        return expenseService.getExpenseById(id);
    }

    @GetMapping("/category/{category}")
    public List<Expense> getExpensesByCategory(@PathVariable String category) {
        return expenseService.getExpensesByCategory(category);
    }
    @GetMapping("/date")
    public List<Expense> getExpensesByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        return expenseService.getExpensesByDateRange(start, end);
    }
    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Integer id,
                                 @RequestBody ExpenseRequest request) {
        return expenseService.updateExpense(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Integer id) {
        return expenseService.deleteExpense(id);
    }
    @GetMapping("/total")
    public Double getTotalExpense() {
        return expenseService.getTotalExpense();
    }
    @GetMapping("/highest")
    public Expense getHighestExpense() {
        return expenseService.getHighestExpense();
    }
    @GetMapping("/monthly")
    public Double getMonthlyExpense() {
        return expenseService.getMonthlyExpense();
    }
    @GetMapping("/category-summary")
    public List<Object[]> getCategorySummary() {
        return expenseService.getCategorySummary();
    }
}