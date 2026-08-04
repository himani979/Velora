package com.velora.backend.controller;

import com.velora.backend.dto.MonthlySummaryResponse;
import com.velora.backend.dto.CategorySummaryResponse;
import java.util.List;
import com.velora.backend.dto.DashboardSummaryResponse;
import com.velora.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
    @GetMapping("/category")
    public List<CategorySummaryResponse> getCategorySummary() {
        return dashboardService.getCategorySummary();
    }
    @GetMapping("/monthly")
    public List<MonthlySummaryResponse> getMonthlySummary() {
        return dashboardService.getMonthlySummary();
    }
}