package com.velora.backend.controller;

import com.velora.backend.dto.IncomeRequest;
import com.velora.backend.entity.Income;
import com.velora.backend.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @PostMapping
    public Income saveIncome(@RequestBody IncomeRequest request) {
        return incomeService.saveIncome(request);
    }

    @GetMapping
    public List<Income> getAllIncome() {
        return incomeService.getAllIncome();
    }
    @PutMapping("/{id}")
    public Income updateIncome(@PathVariable Integer id,
                               @RequestBody IncomeRequest request) {

        return incomeService.updateIncome(id, request);
    }
    @DeleteMapping("/{id}")
    public String deleteIncome(@PathVariable Integer id) {

        return incomeService.deleteIncome(id);
    }
}