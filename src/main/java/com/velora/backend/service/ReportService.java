package com.velora.backend.service;

import com.velora.backend.dto.ReportResponse;
import com.velora.backend.entity.Expense;
import com.velora.backend.entity.User;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import com.velora.backend.dto.IncomeExpenseReportResponse;
import com.velora.backend.repository.IncomeRepository;
import com.velora.backend.entity.Income;
import java.util.*;


@Service
public class ReportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeRepository incomeRepository;
    public List<ReportResponse> getCategoryReport() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new ArrayList<>();
        }

        List<Expense> expenses = expenseRepository.findByUser(user);

        Map<String, Double> categoryMap = new HashMap<>();

        for (Expense expense : expenses) {

            categoryMap.put(
                    expense.getCategory(),
                    categoryMap.getOrDefault(
                            expense.getCategory(),
                            0.0
                    ) + expense.getAmount()
            );
        }

        List<ReportResponse> response = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            response.add(
                    new ReportResponse(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }
    public List<ReportResponse> getMonthlyReport() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new ArrayList<>();
        }

        List<Expense> expenses = expenseRepository.findByUser(user);

        Map<String, Double> monthlyMap = new HashMap<>();

        for (Expense expense : expenses) {

            String month = expense.getExpenseDate()
                    .getMonth()
                    .toString();

            monthlyMap.put(
                    month,
                    monthlyMap.getOrDefault(month, 0.0)
                            + expense.getAmount()
            );
        }

        List<ReportResponse> response = new ArrayList<>();

        for (Map.Entry<String, Double> entry : monthlyMap.entrySet()) {

            response.add(
                    new ReportResponse(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }
    public List<ReportResponse> getDateRangeReport(LocalDate startDate,
                                                   LocalDate endDate) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new ArrayList<>();
        }

        List<Expense> expenses =
                expenseRepository.findByUserAndExpenseDateBetween(
                        user,
                        startDate,
                        endDate
                );

        List<ReportResponse> response = new ArrayList<>();

        for (Expense expense : expenses) {

            response.add(
                    new ReportResponse(
                            expense.getCategory(),
                            expense.getAmount()
                    )
            );
        }

        return response;
    }
    public List<ReportResponse> getIncomeReport() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new ArrayList<>();
        }

        List<Income> incomes = incomeRepository.findByUser(user);

        List<ReportResponse> response = new ArrayList<>();

        for (Income income : incomes) {

            response.add(
                    new ReportResponse(
                            income.getSource(),
                            income.getAmount()
                    )
            );
        }

        return response;
    }
    public IncomeExpenseReportResponse getIncomeExpenseReport() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new IncomeExpenseReportResponse(0.0, 0.0, 0.0);
        }

        double totalIncome = incomeRepository.findByUser(user)
                .stream()
                .mapToDouble(Income::getAmount)
                .sum();

        double totalExpense = expenseRepository.findByUser(user)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        double savings = totalIncome - totalExpense;

        return new IncomeExpenseReportResponse(
                totalIncome,
                totalExpense,
                savings
        );
    }
}