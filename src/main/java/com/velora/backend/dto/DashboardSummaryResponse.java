package com.velora.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class DashboardSummaryResponse {

    private Double totalExpense;

    private Long totalTransactions;

    private Double highestExpense;
    public DashboardSummaryResponse(Double totalExpense,
                                    Long totalTransactions,
                                    Double highestExpense) {
        this.totalExpense = totalExpense;
        this.totalTransactions = totalTransactions;
        this.highestExpense = highestExpense;
    }
}