package com.velora.backend.service;

import com.velora.backend.dto.SavingsSummaryResponse;
import com.velora.backend.repository.IncomeRepository;
import com.velora.backend.repository.BudgetRepository;
import com.velora.backend.entity.Income;
import com.velora.backend.entity.Budget;
import com.velora.backend.dto.MonthlySummaryResponse;
import java.time.Month;
import com.velora.backend.dto.CategorySummaryResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.velora.backend.dto.DashboardSummaryResponse;
import com.velora.backend.entity.Expense;
import com.velora.backend.entity.User;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.velora.backend.repository.SubscriptionRepository;
import com.velora.backend.entity.Subscription;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardSummaryResponse getSummary() {

        // Logged-in user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new DashboardSummaryResponse(
                    0L,
                    0.0,
                    0.0,
                    0.0,
                    0L
            );
        }

        // Get all expenses of the user
        List<Expense> expenses = expenseRepository.findByUser(user);

        // Calculate total expense
        double totalExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // Total transactions
        long totalTransactions = expenseRepository.countByUser(user);

        // Highest expense
        double highestExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .max()
                .orElse(0.0);

        // Get subscriptions
        List<Subscription> subscriptions =
                subscriptionRepository.findByUser(user);

        double monthlySubscriptionAmount = 0.0;
        long activeSubscriptions = 0;

        for (Subscription subscription : subscriptions) {

            if ("ACTIVE".equalsIgnoreCase(subscription.getStatus())) {

                activeSubscriptions++;

                if ("MONTHLY".equalsIgnoreCase(
                        subscription.getBillingCycle())) {

                    monthlySubscriptionAmount += subscription.getAmount();
                }
            }
        }

        return new DashboardSummaryResponse(
                totalTransactions,
                totalExpense,
                highestExpense,
                monthlySubscriptionAmount,
                activeSubscriptions
        );
    }
    public List<CategorySummaryResponse> getCategorySummary() {

        // Get logged-in user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Find logged-in user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new ArrayList<>();
        }

        // Get all expenses of this user
        List<Expense> expenses = expenseRepository.findByUser(user);

        // Store total amount for each category
        Map<String, Double> categoryMap = new HashMap<>();

        for (Expense expense : expenses) {

            categoryMap.put(
                    expense.getCategory(),
                    categoryMap.getOrDefault(expense.getCategory(), 0.0)
                            + expense.getAmount()
            );
        }

        // Convert Map into List<CategorySummaryResponse>
        List<CategorySummaryResponse> response = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            response.add(
                    new CategorySummaryResponse(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    public List<MonthlySummaryResponse> getMonthlySummary() {

        // Logged-in user
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

        List<MonthlySummaryResponse> response = new ArrayList<>();

        for (Map.Entry<String, Double> entry : monthlyMap.entrySet()) {

            response.add(
                    new MonthlySummaryResponse(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }
    public SavingsSummaryResponse getSavingsSummary() {

        // Logged-in user
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new SavingsSummaryResponse(
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    "No Budget"
            );
        }

        // Total Income
        List<Income> incomes = incomeRepository.findByUser(user);

        double totalIncome = incomes.stream()
                .mapToDouble(Income::getAmount)
                .sum();

        // Total Expense
        List<Expense> expenses = expenseRepository.findByUser(user);

        double totalExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // Total Budget
        List<Budget> budgets = budgetRepository.findByUser(user);

        double totalBudget = budgets.stream()
                .mapToDouble(Budget::getAmount)
                .sum();

        // Savings
        double savings = totalIncome - totalExpense;

        // Remaining Budget
        double remainingBudget = totalBudget - totalExpense;
        double budgetUsagePercentage = 0.0;

        if (totalBudget > 0) {
            budgetUsagePercentage = (totalExpense / totalBudget) * 100;
        }

        String budgetStatus = remainingBudget >= 0
                ? "Within Budget"
                : "Budget Exceeded";
        return new SavingsSummaryResponse(
                totalIncome,
                totalExpense,
                totalBudget,
                savings,
                remainingBudget,
                budgetUsagePercentage,
                budgetStatus
        );
    }
    }
