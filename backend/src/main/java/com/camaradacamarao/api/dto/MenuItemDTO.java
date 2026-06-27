package com.camaradacamarao.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MenuItemDTO {

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @Positive
    private BigDecimal price;

    private String description;

    private List<Long> ingredientIds;
}
