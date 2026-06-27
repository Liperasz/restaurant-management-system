package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.StockDTO;
import com.camaradacamarao.api.model.Ingredient;
import com.camaradacamarao.api.model.Stock;
import com.camaradacamarao.api.repository.IngredientRepository;
import com.camaradacamarao.api.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final IngredientRepository ingredientRepository;

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Stock addBatch(StockDTO dto) {
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        Stock stock = new Stock();
        stock.setQuantity(dto.getQuantity());
        stock.setBatch(dto.getBatch());
        stock.setExpirationDate(dto.getExpirationDate());
        stock.setIngredient(ingredient);

        return stockRepository.save(stock);
    }

    public Stock update(Long id, StockDTO dto) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found with id: " + id));

        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        stock.setQuantity(dto.getQuantity());
        stock.setBatch(dto.getBatch());
        stock.setExpirationDate(dto.getExpirationDate());
        stock.setIngredient(ingredient);

        return stockRepository.save(stock);
    }
}
