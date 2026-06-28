package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.StockDTO;
import com.camaradacamarao.api.dto.StockResponseDTO;
import com.camaradacamarao.api.model.Stock;
import com.camaradacamarao.api.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public List<StockResponseDTO> listStock() {
        return stockService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockResponseDTO addBatch(@RequestBody @Valid StockDTO dto) {
        return toDTO(stockService.addBatch(dto));
    }

    @PutMapping("/{id}")
    public StockResponseDTO updateBatch(@PathVariable Long id, @RequestBody @Valid StockDTO dto) {
        return toDTO(stockService.update(id, dto));
    }

    // -------------------------------------------------------------------------
    // ManageStock.jsx consumes:
    //   item.id, item.ingredient.name, item.ingredient.unit,
    //   item.batch, item.quantity, item.expirationDate
    // -------------------------------------------------------------------------
    private StockResponseDTO toDTO(Stock stock) {
        StockResponseDTO dto = new StockResponseDTO();
        dto.setId(stock.getId());
        dto.setQuantity(stock.getQuantity());
        dto.setBatch(stock.getBatch());
        dto.setExpirationDate(stock.getExpirationDate());

        if (stock.getIngredient() != null) {
            StockResponseDTO.IngredientInfo ing = new StockResponseDTO.IngredientInfo();
            ing.setId(stock.getIngredient().getId());
            ing.setName(stock.getIngredient().getName());
            ing.setUnit(stock.getIngredient().getUnit());
            dto.setIngredient(ing);
        }
        return dto;
    }
}
