package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.OrderCreateDTO;
import com.camaradacamarao.api.dto.StatusUpdateDTO;
import com.camaradacamarao.api.model.Order;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.service.OrderService;
import com.camaradacamarao.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody @Valid OrderCreateDTO dto, Authentication authentication) {
        return orderService.create(dto, authentication.getName());
    }

    @GetMapping
    public List<Order> listActive() {
        return orderService.findActive();
    }

    @GetMapping("/history")
    public List<Order> history() {
        return orderService.findAll();
    }

    @GetMapping("/mine")
    public List<Order> myOrders(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return orderService.findByCustomer(user.getId());
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody @Valid StatusUpdateDTO dto) {
        return orderService.updateStatus(id, dto.getStatus());
    }
}
