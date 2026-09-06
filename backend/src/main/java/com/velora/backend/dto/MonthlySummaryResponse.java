package com.velora.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MonthlySummaryResponse {

    private String month;
    private Double amount;

    public MonthlySummaryResponse(String month, Double amount) {
        this.month = month;
        this.amount = amount;
    }
}