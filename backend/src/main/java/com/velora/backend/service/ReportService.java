
package com.velora.backend.service;

import com.velora.backend.dto.ReportResponse;
import com.velora.backend.dto.IncomeExpenseReportResponse;
import com.velora.backend.entity.Expense;
import com.velora.backend.entity.Income;
import com.velora.backend.entity.User;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.repository.IncomeRepository;
import com.velora.backend.repository.UserRepository;
import com.velora.backend.util.CategoryUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeRepository incomeRepository;


    // =========================================
    // GET CURRENT LOGGED-IN USER
    // =========================================

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }


    // =========================================
    // CATEGORY REPORT
    // =========================================

    public List<ReportResponse> getCategoryReport() {

        User user = getCurrentUser();

        if (user == null) {
            return new ArrayList<>();
        }

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Map<String, Double> categoryMap =
                new LinkedHashMap<>();

        for (Expense expense : expenses) {

            String category = CategoryUtils.normalize(
                    expense.getCategory()
            );

            double amount = expense.getAmount() == null
                    ? 0
                    : expense.getAmount();

            categoryMap.merge(
                    category,
                    amount,
                    Double::sum
            );
        }

        List<ReportResponse> response =
                new ArrayList<>();

        for (Map.Entry<String, Double> entry :
                categoryMap.entrySet()) {

            response.add(
                    new ReportResponse(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }


    // =========================================
    // MONTHLY EXPENSE REPORT
    // =========================================

    public List<ReportResponse> getMonthlyReport() {

        User user = getCurrentUser();

        if (user == null) {
            return new ArrayList<>();
        }

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Map<String, Double> monthlyMap =
                new LinkedHashMap<>();

        for (Expense expense : expenses) {

            if (expense.getExpenseDate() == null) {
                continue;
            }

            String month = expense.getExpenseDate()
                    .getMonth()
                    .toString();

            double amount = expense.getAmount() == null
                    ? 0
                    : expense.getAmount();

            monthlyMap.merge(
                    month,
                    amount,
                    Double::sum
            );
        }

        List<ReportResponse> response =
                new ArrayList<>();

        for (Map.Entry<String, Double> entry :
                monthlyMap.entrySet()) {

            response.add(
                    new ReportResponse(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }


    // =========================================
    // DATE RANGE REPORT
    // =========================================

    public List<ReportResponse> getDateRangeReport(
            LocalDate startDate,
            LocalDate endDate) {

        User user = getCurrentUser();

        if (user == null) {
            return new ArrayList<>();
        }

        List<Expense> expenses =
                expenseRepository.findByUserAndExpenseDateBetween(
                        user,
                        startDate,
                        endDate
                );

        Map<String, Double> categoryMap =
                new LinkedHashMap<>();

        for (Expense expense : expenses) {

            String category = CategoryUtils.normalize(
                    expense.getCategory()
            );

            double amount = expense.getAmount() == null
                    ? 0
                    : expense.getAmount();

            categoryMap.merge(
                    category,
                    amount,
                    Double::sum
            );
        }

        List<ReportResponse> response =
                new ArrayList<>();

        for (Map.Entry<String, Double> entry :
                categoryMap.entrySet()) {

            response.add(
                    new ReportResponse(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }


    // =========================================
    // INCOME REPORT
    // =========================================

    public List<ReportResponse> getIncomeReport() {

        User user = getCurrentUser();

        if (user == null) {
            return new ArrayList<>();
        }

        List<Income> incomes =
                incomeRepository.findByUser(user);

        List<ReportResponse> response =
                new ArrayList<>();

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


    // =========================================
    // INCOME VS EXPENSE REPORT
    // =========================================

    public IncomeExpenseReportResponse getIncomeExpenseReport() {

        User user = getCurrentUser();

        if (user == null) {
            return new IncomeExpenseReportResponse(
                    0.0,
                    0.0,
                    0.0
            );
        }

        double totalIncome =
                incomeRepository.findByUser(user)
                        .stream()
                        .mapToDouble(income ->
                                income.getAmount() == null
                                        ? 0
                                        : income.getAmount()
                        )
                        .sum();

        double totalExpense =
                expenseRepository.findByUser(user)
                        .stream()
                        .mapToDouble(expense ->
                                expense.getAmount() == null
                                        ? 0
                                        : expense.getAmount()
                        )
                        .sum();

        double savings = totalIncome - totalExpense;

        return new IncomeExpenseReportResponse(
                totalIncome,
                totalExpense,
                savings
        );
    }
}
