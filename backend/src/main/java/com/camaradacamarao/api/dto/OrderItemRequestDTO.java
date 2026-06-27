package com.camaradacamarao.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequestDTO {

    @NotNull
    private Long menuItemId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
