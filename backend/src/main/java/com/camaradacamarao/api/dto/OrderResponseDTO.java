package com.camaradacamarao.api.dto;

import com.camaradacamarao.api.model.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private boolean paid;
    private OrderStatus status;
    private UserResponseDTO customer;
    private UserResponseDTO attendant;
    private List<OrderItemResponseDTO> items;
}
