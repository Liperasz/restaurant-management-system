package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.MenuItemDTO;
import com.camaradacamarao.api.model.Ingredient;
import com.camaradacamarao.api.model.MenuItem;
import com.camaradacamarao.api.repository.IngredientRepository;
import com.camaradacamarao.api.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;

    public List<MenuItem> findAllActive() {
        return menuItemRepository.findByActiveTrue();
    }

    @Transactional
    public MenuItem create(MenuItemDTO dto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(dto.getName());
        menuItem.setPrice(dto.getPrice());
        menuItem.setDescription(dto.getDescription());
        menuItem.setActive(true);

        if (dto.getIngredientIds() != null && !dto.getIngredientIds().isEmpty()) {
            List<Ingredient> ingredients = ingredientRepository.findAllById(dto.getIngredientIds());
            menuItem.setIngredients(ingredients);
        }

        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem update(Long id, MenuItemDTO dto) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found with id: " + id));

        menuItem.setName(dto.getName());
        menuItem.setPrice(dto.getPrice());
        menuItem.setDescription(dto.getDescription());

        if (dto.getIngredientIds() != null) {
            List<Ingredient> ingredients = ingredientRepository.findAllById(dto.getIngredientIds());
            menuItem.setIngredients(ingredients);
        } else {
            menuItem.getIngredients().clear();
        }

        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public void deactivate(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found with id: " + id));
        
        menuItem.setActive(false);
        menuItemRepository.save(menuItem);
    }
}
