
package com.velora.backend.service;

import com.velora.backend.dto.FutureLabRequest;
import com.velora.backend.entity.Expense;
import com.velora.backend.entity.Income;
import com.velora.backend.entity.User;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.repository.IncomeRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FutureLabService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public FutureLabService(
            IncomeRepository incomeRepository,
            ExpenseRepository expenseRepository) {

        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
    }

    public Map<String, Object> simulate(
            User user,
            FutureLabRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Simulation details are required."
            );
        }

        int months = request.getMonths() == null
                ? 6
                : request.getMonths();

        if (months != 3 && months != 6 && months != 12) {
            throw new IllegalArgumentException(
                    "Months must be 3, 6 or 12."
            );
        }

        double extraSavings = nonNegative(
                request.getExtraMonthlySavings(),
                "Extra monthly savings"
        );

        double expenseIncreasePercent = nonNegative(
                request.getExpenseIncreasePercent(),
                "Expense increase percentage"
        );

        double cancelledSubscription = nonNegative(
                request.getCancelledSubscriptionAmount(),
                "Cancelled subscription amount"
        );

        // Previous 3 complete calendar months

        LocalDate currentMonthStart =
                LocalDate.now().withDayOfMonth(1);

        LocalDate historyStart =
                currentMonthStart.minusMonths(3);

        LocalDate historyEnd =
                currentMonthStart.minusDays(1);

        List<Income> incomes =
                incomeRepository.findByUser(user);

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        double historicalIncome = incomes.stream()
                .filter(income ->
                        income.getIncomeDate() != null
                                && !income.getIncomeDate()
                                .isBefore(historyStart)
                                && !income.getIncomeDate()
                                .isAfter(historyEnd)
                )
                .mapToDouble(income ->
                        income.getAmount() == null
                                ? 0
                                : income.getAmount()
                )
                .sum();

        double historicalExpenses = expenses.stream()
                .filter(expense ->
                        expense.getExpenseDate() != null
                                && !expense.getExpenseDate()
                                .isBefore(historyStart)
                                && !expense.getExpenseDate()
                                .isAfter(historyEnd)
                )
                .mapToDouble(expense ->
                        expense.getAmount() == null
                                ? 0
                                : expense.getAmount()
                )
                .sum();

        double averageIncome =
                historicalIncome / 3.0;

        double averageExpenses =
                historicalExpenses / 3.0;

        // User input overrides historical averages.

        double monthlyIncome =
                request.getMonthlyIncome() == null
                        ? averageIncome
                        : nonNegative(
                        request.getMonthlyIncome(),
                        "Monthly income"
                );

        double monthlyExpenses =
                request.getMonthlyExpenses() == null
                        ? averageExpenses
                        : nonNegative(
                        request.getMonthlyExpenses(),
                        "Monthly expenses"
                );

        // Apply the selected scenario.

        double increasedExpenses =
                monthlyExpenses
                        * (1 + expenseIncreasePercent / 100.0);

        if (!Double.isFinite(increasedExpenses)) {
            throw new IllegalArgumentException(
                    "Expense increase is too large."
            );
        }

        /*
         * Subscription cancellation and extra savings
         * represent reductions in planned spending.
         *
         * Expenses cannot become negative.
         */

        double afterCancellation = Math.max(
                0,
                increasedExpenses - cancelledSubscription
        );

        double adjustedExpenses = Math.max(
                0,
                afterCancellation - extraSavings
        );

        double actualExtraSavings =
                afterCancellation - adjustedExpenses;

        double monthlySavings =
                monthlyIncome - adjustedExpenses;

        // Calculate monthly projection.

        List<Map<String, Object>> projection =
                new ArrayList<>();

        double cumulativeSavings = 0;

        YearMonth firstProjectedMonth =
                YearMonth.from(LocalDate.now())
                        .plusMonths(1);

        for (int month = 1; month <= months; month++) {

            cumulativeSavings += monthlySavings;

            Map<String, Object> point =
                    new LinkedHashMap<>();

            point.put("month", month);

            point.put(
                    "label",
                    firstProjectedMonth
                            .plusMonths(month - 1)
                            .toString()
            );

            point.put(
                    "income",
                    round(monthlyIncome)
            );

            point.put(
                    "expenses",
                    round(adjustedExpenses)
            );

            point.put(
                    "monthlySavings",
                    round(monthlySavings)
            );

            point.put(
                    "cumulativeSavings",
                    round(cumulativeSavings)
            );

            projection.add(point);
        }

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "historicalAverageIncome",
                round(averageIncome)
        );

        response.put(
                "historicalAverageExpenses",
                round(averageExpenses)
        );

        response.put(
                "baseMonthlyExpenses",
                round(monthlyExpenses)
        );

        response.put(
                "monthlyIncome",
                round(monthlyIncome)
        );

        response.put(
                "monthlyExpenses",
                round(adjustedExpenses)
        );

        response.put(
                "actualExtraSavings",
                round(actualExtraSavings)
        );

        response.put(
                "monthlySavings",
                round(monthlySavings)
        );

        response.put(
                "projectedSavings",
                round(cumulativeSavings)
        );

        response.put("months", months);

        response.put("projection", projection);

        response.put(
                "note",
                "These estimates assume the same income " +
                        "and adjusted expenses every month. " +
                        "They do not include an existing savings " +
                        "balance or guarantee future results."
        );

        return response;
    }

    private double nonNegative(
            Double value,
            String fieldName) {

        if (value == null) {
            return 0;
        }

        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid non-negative number."
            );
        }

        return value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
