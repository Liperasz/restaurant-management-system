package com.camaradacamarao.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StockDTO {

    @NotNull
    @Min(0)
    private Integer quantity;

    private String batch;

    private LocalDate expirationDate;

    @NotNull
    private Long ingredientId;
}
