package com.velora.backend.repository;

import com.velora.backend.entity.Expense;
import com.velora.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByUser(User user);
    Long countByUser(User user);

    List<Expense> findByUserOrderByAmountDesc(User user);
    List<Expense> findByUserOrderByCategory(User user);
    List<Expense> findByUserAndExpenseDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
    List<Expense> findByUserAndTitleContainingIgnoreCase(User user, String title);
    List<Expense> findByUserAndCategoryIgnoreCase(User user, String category);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user")
    Double getTotalExpenseByUser(@Param("user") User user);

    Expense findTopByUserOrderByAmountDesc(User user);
    @Query("""
       SELECT COALESCE(SUM(e.amount),0)
       FROM Expense e
       WHERE e.user = :user
       AND MONTH(e.expenseDate) = :month
       AND YEAR(e.expenseDate) = :year
       """)
    Double getMonthlyExpense(
            @Param("user") User user,
            @Param("month") int month,
            @Param("year") int year
    );
    @Query("""
SELECT e.category, SUM(e.amount)
FROM Expense e
WHERE e.user = :user
GROUP BY e.category
""")
    List<Object[]> getCategorySummary(@Param("user") User user);


}