package com.camaradacamarao.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
