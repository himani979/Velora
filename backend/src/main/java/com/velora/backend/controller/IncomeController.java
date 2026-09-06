package com.velora.backend.controller;

import com.velora.backend.dto.IncomeRequest;
import com.velora.backend.entity.Income;
import com.velora.backend.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.velora.backend.dto.IncomeResponse;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @PostMapping
    public IncomeResponse saveIncome(
            @Valid @RequestBody IncomeRequest request) {

        Income income = incomeService.saveIncome(request);

        if (income == null) {
            return null;
        }

        return new IncomeResponse(
                income.getId(),
                income.getSource(),
                income.getAmount(),
                income.getIncomeDate(),
                income.getNote()
        );
    }

    @GetMapping
    public List<IncomeResponse> getAllIncome() {

        List<Income> incomes = incomeService.getAllIncome();

        return incomes.stream()
                .map(income -> new IncomeResponse(
                        income.getId(),
                        income.getSource(),
                        income.getAmount(),
                        income.getIncomeDate(),
                        income.getNote()
                ))
                .toList();
    }
    @GetMapping("/total")
    public Double getTotalIncome() {
        return incomeService.getTotalIncome();
    }
    @PutMapping("/{id}")
    public IncomeResponse updateIncome(
            @PathVariable Integer id,
            @Valid @RequestBody IncomeRequest request) {

        Income income = incomeService.updateIncome(id, request);

        if (income == null) {
            return null;
        }

        return new IncomeResponse(
                income.getId(),
                income.getSource(),
                income.getAmount(),
                income.getIncomeDate(),
                income.getNote()
        );
    }

    @DeleteMapping("/{id}")
    public String deleteIncome(@PathVariable Integer id) {

        return incomeService.deleteIncome(id);
    }
}