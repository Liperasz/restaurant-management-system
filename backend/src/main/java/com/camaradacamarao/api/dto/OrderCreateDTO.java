package com.camaradacamarao.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {

    @NotEmpty
    @Valid
    private List<OrderItemRequestDTO> items;
}
