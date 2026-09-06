package com.velora.backend.controller;

import com.velora.backend.dto.ReportResponse;
import com.velora.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import com.velora.backend.dto.IncomeExpenseReportResponse;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/category")
    public List<ReportResponse> getCategoryReport() {
        return reportService.getCategoryReport();
    }
    @GetMapping("/monthly")
    public List<ReportResponse> getMonthlyReport() {
        return reportService.getMonthlyReport();
    }
    @GetMapping("/date-range")
    public List<ReportResponse> getDateRangeReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return reportService.getDateRangeReport(
                startDate,
                endDate
        );
    }
    @GetMapping("/income")
    public List<ReportResponse> getIncomeReport() {
        return reportService.getIncomeReport();
    }
    @GetMapping("/income-expense")
    public IncomeExpenseReportResponse getIncomeExpenseReport() {
        return reportService.getIncomeExpenseReport();
    }

}