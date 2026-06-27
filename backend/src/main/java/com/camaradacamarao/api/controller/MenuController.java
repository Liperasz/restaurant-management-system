package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.MenuItemDTO;
import com.camaradacamarao.api.model.MenuItem;
import com.camaradacamarao.api.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemService menuItemService;

    @GetMapping
    public List<MenuItem> listActive() {
        return menuItemService.findAllActive();
    }

    @GetMapping("/{id}")
    public MenuItem getById(@PathVariable Long id) {
        return menuItemService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItem create(@RequestBody @Valid MenuItemDTO dto) {
        return menuItemService.create(dto);
    }

    @PutMapping("/{id}")
    public MenuItem update(@PathVariable Long id, @RequestBody @Valid MenuItemDTO dto) {
        return menuItemService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        menuItemService.deactivate(id);
    }
}
