package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.OrderCreateDTO;
import com.camaradacamarao.api.dto.OrderItemResponseDTO;
import com.camaradacamarao.api.dto.OrderResponseDTO;
import com.camaradacamarao.api.dto.StatusUpdateDTO;
import com.camaradacamarao.api.dto.UserResponseDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO create(@RequestBody @Valid OrderCreateDTO dto, Authentication authentication) {
        return toDTO(orderService.create(dto, authentication.getName()));
    }

    @GetMapping
    public List<OrderResponseDTO> listActive() {
        return orderService.findActive().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/history")
    public List<OrderResponseDTO> history() {
        return orderService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/mine")
    public List<OrderResponseDTO> myOrders(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return orderService.findByCustomer(user.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/status")
    public OrderResponseDTO updateStatus(@PathVariable Long id, @RequestBody @Valid StatusUpdateDTO dto) {
        return toDTO(orderService.updateStatus(id, dto.getStatus()));
    }

    // -------------------------------------------------------------------------
    // Mapping helpers — convert entity → safe DTO (no password, no internals)
    // -------------------------------------------------------------------------

    private OrderResponseDTO toDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setPaid(order.isPaid());
        dto.setStatus(order.getStatus());

        if (order.getCustomer() != null) {
            dto.setCustomer(toUserDTO(order.getCustomer()));
        }
        if (order.getAttendant() != null) {
            dto.setAttendant(toUserDTO(order.getAttendant()));
        }
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemResponseDTO itemDto = new OrderItemResponseDTO();
                itemDto.setId(item.getId());
                itemDto.setMenuItemId(item.getMenuItem().getId());
                itemDto.setMenuItemName(item.getMenuItem().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    private UserResponseDTO toUserDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCpf(user.getCpf());
        dto.setPhone(user.getPhone());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole());
        // password is intentionally NOT set — it is @JsonIgnore on User but we
        // also protect it here as a second layer: UserResponseDTO has no password field.
        return dto;
    }
}
