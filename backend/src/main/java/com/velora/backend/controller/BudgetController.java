package com.velora.backend.controller;

import java.util.List;

import com.velora.backend.dto.BudgetRequest;
import com.velora.backend.dto.BudgetResponse;
import com.velora.backend.entity.Budget;
import com.velora.backend.service.BudgetService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;


    // =====================================================
    // ADD BUDGET
    // =====================================================

    @PostMapping
    public Budget saveBudget(
            @Valid @RequestBody BudgetRequest request) {

        return budgetService.saveBudget(request);
    }


    // =====================================================
    // GET BUDGETS FOR SELECTED MONTH
    // =====================================================

    @GetMapping
    public List<BudgetResponse> getAllBudgets(
            @RequestParam int month,
            @RequestParam int year) {

        return budgetService.getAllBudgetResponses(
                month,
                year
        );
    }


    // =====================================================
    // UPDATE BUDGET
    // =====================================================

    @PutMapping("/{id}")
    public BudgetResponse updateBudget(
            @PathVariable Integer id,
            @Valid @RequestBody BudgetRequest request) {

        Budget budget =
                budgetService.updateBudget(
                        id,
                        request
                );

        if (budget == null) {
            return null;
        }

        /*
         * Return the updated budget with:
         * spent
         * remaining
         * progress
         */

        List<BudgetResponse> budgets =
                budgetService.getAllBudgetResponses(
                        request.getMonth(),
                        request.getYear()
                );

        return budgets.stream()
                .filter(b ->
                        b.getId().equals(id)
                )
                .findFirst()
                .orElse(null);
    }


    // =====================================================
    // DELETE BUDGET
    // =====================================================

    @DeleteMapping("/{id}")
    public String deleteBudget(
            @PathVariable Integer id) {

        return budgetService.deleteBudget(id);
    }
}