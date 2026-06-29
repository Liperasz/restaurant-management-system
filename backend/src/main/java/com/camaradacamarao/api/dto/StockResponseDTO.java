package com.camaradacamarao.api.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * Safe Stock response — exposes what ManageStock.jsx consumes:
 *   item.id, item.ingredient.name, item.ingredient.unit,
 *   item.batch, item.quantity, item.expirationDate
 *
 * Uses a nested IngredientInfo inner class to mirror the frontend's
 * `item.ingredient.name` and `item.ingredient.unit` field paths.
 */
@Data
public class StockResponseDTO {

    private Long id;
    private Integer quantity;
    private String batch;
    private LocalDate expirationDate;
    private IngredientInfo ingredient;

    @Data
    public static class IngredientInfo {
        private Long id;
        private String name;
        private String unit;
    }
}
