package com.velora.backend.controller;

import com.velora.backend.dto.ExpenseRequest;
import com.velora.backend.entity.Expense;
import com.velora.backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public Expense saveExpense(@RequestBody ExpenseRequest request) {
        return expenseService.saveExpense(request);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Integer id,
                                 @RequestBody ExpenseRequest request) {

        return expenseService.updateExpense(id, request);
    }

    @GetMapping
    public List<Expense> getAllExpenses(){
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public Expense getExpense(@PathVariable Integer id){
        return expenseService.getExpenseById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Integer id) {
        return expenseService.deleteExpense(id);
    }
}