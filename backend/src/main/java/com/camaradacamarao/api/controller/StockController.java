package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.StockDTO;
import com.camaradacamarao.api.model.Stock;
import com.camaradacamarao.api.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public List<Stock> listStock() {
        return stockService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Stock addBatch(@RequestBody @Valid StockDTO dto) {
        return stockService.addBatch(dto);
    }

    @PutMapping("/{id}")
    public Stock updateBatch(@PathVariable Long id, @RequestBody @Valid StockDTO dto) {
        return stockService.update(id, dto);
    }
}
