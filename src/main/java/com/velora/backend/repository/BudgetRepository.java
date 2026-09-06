package com.velora.backend.repository;

import com.velora.backend.entity.Budget;
import com.velora.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    List<Budget> findByUser(User user);

    @Query("""
        SELECT b
        FROM Budget b
        WHERE b.user = :user
        AND b.month = :month
        AND b.year = :year
        """)
    List<Budget> findByUserAndMonthAndYear(
            @Param("user") User user,
            @Param("month") int month,
            @Param("year") int year
    );

    Optional<Budget> findByUserAndCategoryIgnoreCaseAndMonthAndYear(
            User user,
            String category,
            int month,
            int year
    );
}