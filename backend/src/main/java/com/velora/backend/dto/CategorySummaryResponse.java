package com.velora.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class CategorySummaryResponse {

    private String category;

    private Double amount;
    public CategorySummaryResponse(String category, Double amount) {
        this.category = category;
        this.amount = amount;
    }
}