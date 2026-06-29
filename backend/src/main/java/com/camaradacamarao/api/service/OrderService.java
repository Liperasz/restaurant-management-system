package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.OrderCreateDTO;
import com.camaradacamarao.api.dto.OrderItemRequestDTO;
import com.camaradacamarao.api.model.MenuItem;
import com.camaradacamarao.api.model.Order;
import com.camaradacamarao.api.model.OrderItem;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.OrderStatus;
import com.camaradacamarao.api.repository.MenuItemRepository;
import com.camaradacamarao.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserService userService; // P06 fix — use service, not repository

    /**
     * Valid status transitions (state machine).
     * A status is only allowed if the current status is in its allowed predecessors.
     *
     * PENDING   → PREPARING, CANCELLED
     * PREPARING → DELIVERED, CANCELLED
     * DELIVERED → (terminal — no transitions)
     * CANCELLED → (terminal — no transitions)
     */
    private static final java.util.Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS =
            java.util.Map.of(
                    OrderStatus.PENDING,   Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
                    OrderStatus.PREPARING, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
                    OrderStatus.DELIVERED, Set.of(),
                    OrderStatus.CANCELLED, Set.of()
            );

    @Transactional
    public Order create(OrderCreateDTO dto, String customerEmail) {
        User customer = userService.findByEmail(customerEmail); // P06 — via service

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setPaid(false);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Item do cardápio não encontrado com id: " + itemDto.getMenuItemId()));

            if (!menuItem.isActive()) {
                throw new IllegalArgumentException(
                        "Item do cardápio não está disponível: " + menuItem.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(menuItem.getPrice());

            items.add(orderItem);

            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        order.setItems(items);
        order.setTotalPrice(totalPrice);

        return orderRepository.save(order);
    }

    public List<Order> findActive() {
        return orderRepository.findByStatusNotIn(
                List.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED)
        );
    }

    /**
     * P04 — Validates status transitions before persisting.
     * Prevents illegal moves like DELIVERED → PENDING.
     */
    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido não encontrado com id: " + id));

        OrderStatus current = order.getStatus();
        Set<OrderStatus> allowed = VALID_TRANSITIONS.getOrDefault(current, Set.of());

        if (!allowed.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Transição de status inválida: " + current + " → " + newStatus +
                    ". Transições permitidas a partir de " + current + ": " + allowed);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<Order> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> findByAttendant(Long attendantId) {
        return orderRepository.findByAttendantId(attendantId);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
