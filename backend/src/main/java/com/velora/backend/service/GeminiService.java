package com.velora.backend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import com.velora.backend.entity.Budget;
import com.velora.backend.entity.Expense;
import com.velora.backend.entity.Income;
import com.velora.backend.entity.User;

import com.velora.backend.repository.BudgetRepository;
import com.velora.backend.repository.ExpenseRepository;
import com.velora.backend.repository.IncomeRepository;
import com.velora.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {

    private final Client client;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;


    public GeminiService(
            @Value("${gemini.api.key}") String apiKey) {

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }


    public String askGemini(String question) {

        // ==========================================
        // GET LOGGED-IN USER
        // ==========================================

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return "Unable to identify the logged-in user.";
        }


        // ==========================================
        // GET USER EXPENSES
        // ==========================================

        List<Expense> expenses =
                expenseRepository.findByUser(user);


        // ==========================================
        // GET USER INCOME
        // ==========================================

        List<Income> incomes =
                incomeRepository.findByUser(user);


        // ==========================================
        // GET USER BUDGETS
        // ==========================================

        List<Budget> budgets =
                budgetRepository.findByUser(user);


        // ==========================================
        // BUILD FINANCIAL DATA
        // ==========================================

        StringBuilder financialData =
                new StringBuilder();


        // ------------------------------------------
        // EXPENSES
        // ------------------------------------------

        financialData.append(
                "\nUSER EXPENSES:\n"
        );

        if (expenses.isEmpty()) {

            financialData.append(
                    "No expenses recorded.\n"
            );

        } else {

            for (Expense expense : expenses) {

                financialData
                        .append("- Category: ")
                        .append(expense.getCategory())

                        .append(", Amount: ₹")
                        .append(expense.getAmount())

                        .append(", Date: ")
                        .append(expense.getExpenseDate())

                        .append(", Title: ")
                        .append(expense.getTitle());

                if (expense.getNote() != null
                        && !expense.getNote().isBlank()) {

                    financialData
                            .append(", Note: ")
                            .append(expense.getNote());
                }

                financialData.append("\n");
            }
        }


        // ------------------------------------------
        // INCOME
        // ------------------------------------------

        financialData.append(
                "\nUSER INCOME:\n"
        );

        if (incomes.isEmpty()) {

            financialData.append(
                    "No income recorded.\n"
            );

        } else {

            for (Income income : incomes) {

                financialData
                        .append("- Source: ")
                        .append(income.getSource())

                        .append(", Amount: ₹")
                        .append(income.getAmount())

                        .append(", Date: ")
                        .append(income.getIncomeDate());

                if (income.getNote() != null
                        && !income.getNote().isBlank()) {

                    financialData
                            .append(", Note: ")
                            .append(income.getNote());
                }

                financialData.append("\n");
            }
        }


        // ------------------------------------------
        // BUDGETS
        // ------------------------------------------

        financialData.append(
                "\nUSER BUDGETS:\n"
        );

        if (budgets.isEmpty()) {

            financialData.append(
                    "No budgets recorded.\n"
            );

        } else {

            for (Budget budget : budgets) {

                financialData
                        .append("- Category: ")
                        .append(budget.getCategory())

                        .append(", Budget Amount: ₹")
                        .append(budget.getAmount())

                        .append(", Month: ")
                        .append(budget.getMonth())

                        .append(", Year: ")
                        .append(budget.getYear())

                        .append("\n");
            }
        }


        // ==========================================
        // GEMINI PROMPT
        // ==========================================

        String prompt = """
                You are Velora AI, a personal finance assistant.

                You help the user understand their personal
                income, expenses and budgets.

                Use ONLY the financial data provided below.

                IMPORTANT RULES:

                1. Never invent financial information.
                2. Never assume expenses or income that are not
                   present in the provided data.
                3. If the data is insufficient, clearly tell the user.
                4. Keep answers clear and easy to understand.
                5. Use Indian Rupee (₹) for monetary amounts.
                6. When comparing budgets and expenses, consider
                   the budget's month and year.
                7. Give useful observations when the data supports them.
                8. Do not expose technical implementation details.
                9. Do not provide professional financial advice.
                10. Answer the user's actual question directly.

                USER QUESTION:
                %s

                FINANCIAL DATA:
                %s

                """.formatted(
                question,
                financialData
        );


        // ==========================================
        // SEND TO GEMINI
        // ==========================================

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.6-flash",
                        prompt,
                        null
                );


        // ==========================================
        // RETURN RESPONSE
        // ==========================================

        return response.text();
    }
}