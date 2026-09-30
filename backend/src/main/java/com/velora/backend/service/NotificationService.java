
package com.velora.backend.service;

import com.velora.backend.entity.Budget;
import com.velora.backend.entity.Expense;
import com.velora.backend.entity.Subscription;
import com.velora.backend.entity.User;

import com.velora.backend.repository.BudgetRepository;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.repository.SubscriptionRepository;

import com.velora.backend.util.CategoryUtils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class NotificationService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final SubscriptionRepository subscriptionRepository;

    public NotificationService(
            BudgetRepository budgetRepository,
            ExpenseRepository expenseRepository,
            SubscriptionRepository subscriptionRepository) {

        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<String> getNotifications(User user) {

        List<String> notifications = new ArrayList<>();

        LocalDate today = LocalDate.now();

        // ==========================================
        // 1. CURRENT MONTH EXPENSES
        // ==========================================

        LocalDate currentMonthStart =
                today.withDayOfMonth(1);

        List<Expense> monthlyExpenses =
                expenseRepository.findByUserAndExpenseDateBetween(
                        user,
                        currentMonthStart,
                        today
                );

        // ==========================================
        // 2. BUDGET ALERTS
        // ==========================================

        List<Budget> budgets =
                budgetRepository.findByUserAndMonthAndYear(
                        user,
                        today.getMonthValue(),
                        today.getYear()
                );

        for (Budget budget : budgets) {

            double spent = monthlyExpenses.stream()
                    .filter(expense ->
                            CategoryUtils.isSame(
                                    expense.getCategory(),
                                    budget.getCategory()
                            )
                    )
                    .mapToDouble(expense ->
                            expense.getAmount() == null
                                    ? 0
                                    : expense.getAmount()
                    )
                    .sum();

            double limit =
                    budget.getAmount() == null
                            ? 0
                            : budget.getAmount();

            if (limit <= 0) {
                continue;
            }

            double percentage =
                    (spent / limit) * 100;

            String category =
                    CategoryUtils.normalize(
                            budget.getCategory()
                    );

            if (percentage > 100) {

                notifications.add(
                        "🚨 " + category
                                + " budget exceeded by ₹"
                                + formatMoney(
                                spent - limit
                        )
                );

            } else if (percentage >= 100) {

                notifications.add(
                        "🚨 " + category
                                + " budget reached."
                );

            } else if (percentage >= 80) {

                notifications.add(
                        "⚠️ You have used "
                                + String.format(
                                Locale.US,
                                "%.0f",
                                percentage
                        )
                                + "% of your "
                                + category
                                + " budget."
                );
            }
        }

        // ==========================================
        // 3. SUBSCRIPTION RENEWAL REMINDERS
        // ==========================================

        List<Subscription> subscriptions =
                subscriptionRepository.findByUser(user);

        for (Subscription subscription : subscriptions) {

            if (!"ACTIVE".equalsIgnoreCase(
                    subscription.getStatus())) {
                continue;
            }

            if (subscription.getNextPaymentDate() == null) {
                continue;
            }

            int reminderDays =
                    subscription.getReminderDaysBefore() == null
                            ? 3
                            : subscription.getReminderDaysBefore();

            LocalDate paymentDate =
                    subscription.getNextPaymentDate();

            if (!paymentDate.isBefore(today)
                    && !paymentDate.isAfter(
                    today.plusDays(reminderDays))) {

                notifications.add(
                        "🔔 "
                                + subscription.getServiceName()
                                + " payment of ₹"
                                + subscription.getAmount()
                                + " is due on "
                                + paymentDate
                );
            }
        }

        // ==========================================
        // 4. UNUSUAL SPENDING ALERTS
        // ==========================================

        /*
         * Compare the current month's category-wise
         * expenses with the previous 3 complete months.
         */

        LocalDate historyStart =
                currentMonthStart.minusMonths(3);

        LocalDate historyEnd =
                currentMonthStart.minusDays(1);

        List<Expense> previousExpenses =
                expenseRepository.findByUserAndExpenseDateBetween(
                        user,
                        historyStart,
                        historyEnd
                );

        // Current month spending by category
        Map<String, Double> currentSpending =
                new HashMap<>();

        for (Expense expense : monthlyExpenses) {

            String category =
                    CategoryUtils.normalize(
                            expense.getCategory()
                    );

            if (category.isBlank()) {
                continue;
            }

            double amount =
                    expense.getAmount() == null
                            ? 0
                            : expense.getAmount();

            currentSpending.merge(
                    category,
                    amount,
                    Double::sum
            );
        }

        // Previous 3 months spending by category
        Map<String, Double> previousSpending =
                new HashMap<>();

        // Track which months contain records
        Map<String, Set<YearMonth>> categoryMonths =
                new HashMap<>();

        for (Expense expense : previousExpenses) {

            if (expense.getExpenseDate() == null) {
                continue;
            }

            String category =
                    CategoryUtils.normalize(
                            expense.getCategory()
                    );

            if (category.isBlank()) {
                continue;
            }

            double amount =
                    expense.getAmount() == null
                            ? 0
                            : expense.getAmount();

            previousSpending.merge(
                    category,
                    amount,
                    Double::sum
            );

            categoryMonths
                    .computeIfAbsent(
                            category,
                            key -> new HashSet<>()
                    )
                    .add(
                            YearMonth.from(
                                    expense.getExpenseDate()
                            )
                    );
        }

        // Check unusual spending
        for (Map.Entry<String, Double> entry :
                currentSpending.entrySet()) {

            String category = entry.getKey();

            double currentAmount =
                    entry.getValue();

            // No previous spending = no reliable baseline
            if (!previousSpending.containsKey(category)) {
                continue;
            }

            /*
             * Require records in at least 2 of the
             * previous 3 months to reduce false alerts.
             */
            Set<YearMonth> recordedMonths =
                    categoryMonths.get(category);

            if (recordedMonths == null
                    || recordedMonths.size() < 2) {
                continue;
            }

            double previousTotal =
                    previousSpending.get(category);

            // Average across all 3 calendar months
            double monthlyAverage =
                    previousTotal / 3.0;

            if (monthlyAverage <= 0) {
                continue;
            }

            double difference =
                    currentAmount - monthlyAverage;

            double percentageIncrease =
                    (difference / monthlyAverage) * 100;

            /*
             * Alert only when:
             * 1. Spending is more than 50% above average.
             * 2. Difference is at least Rs. 500.
             */
            if (percentageIncrease > 50
                    && difference >= 500) {

                notifications.add(
                        "📈 Unusual spending in "
                                + category
                                + ": ₹"
                                + formatMoney(currentAmount)
                                + " spent this month, compared to "
                                + "your previous 3-month average of ₹"
                                + formatMoney(monthlyAverage)
                                + "."
                );
            }
        }

        return notifications;
    }

    // ==========================================
    // FORMAT MONEY
    // ==========================================

    private String formatMoney(double amount) {

        return String.format(
                Locale.US,
                "%.2f",
                amount
        );
    }
}
