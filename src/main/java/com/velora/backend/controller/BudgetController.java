package com.velora.backend.controller;

import java.util.List;
import com.velora.backend.dto.BudgetRequest;
import com.velora.backend.entity.Budget;
import com.velora.backend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    public Budget saveBudget(@RequestBody BudgetRequest request) {
        return budgetService.saveBudget(request);
    }
    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }
    @PutMapping("/{id}")
    public Budget updateBudget(@PathVariable Integer id,
                               @RequestBody BudgetRequest request) {

        return budgetService.updateBudget(id, request);
    }
    @DeleteMapping("/{id}")
    public String deleteBudget(@PathVariable Integer id) {

        return budgetService.deleteBudget(id);
    }
}