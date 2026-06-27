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
import com.camaradacamarao.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order create(OrderCreateDTO dto, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setPaid(false);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new IllegalArgumentException("MenuItem not found with id: " + itemDto.getMenuItemId()));

            if (!menuItem.isActive()) {
                throw new IllegalArgumentException("MenuItem is not active: " + menuItem.getName());
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
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        order.setStatus(status);
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
