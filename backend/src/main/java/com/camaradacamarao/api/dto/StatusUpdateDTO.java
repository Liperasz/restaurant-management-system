package com.camaradacamarao.api.dto;

import com.camaradacamarao.api.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateDTO {

    @NotNull
    private OrderStatus status;
}
