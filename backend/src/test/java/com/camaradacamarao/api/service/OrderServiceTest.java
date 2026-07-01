package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.OrderCreateDTO;
import com.camaradacamarao.api.dto.OrderItemRequestDTO;
import com.camaradacamarao.api.model.MenuItem;
import com.camaradacamarao.api.model.Order;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.OrderStatus;
import com.camaradacamarao.api.repository.MenuItemRepository;
import com.camaradacamarao.api.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    private User customer;
    private MenuItem activeItem;
    private MenuItem inactiveItem;
    private OrderCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@example.com");

        activeItem = new MenuItem();
        activeItem.setId(10L);
        activeItem.setName("Camarão Grelhado");
        activeItem.setPrice(new BigDecimal("50.00"));
        activeItem.setActive(true);

        inactiveItem = new MenuItem();
        inactiveItem.setId(11L);
        inactiveItem.setName("Camarão Indisponível");
        inactiveItem.setPrice(new BigDecimal("40.00"));
        inactiveItem.setActive(false);

        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setMenuItemId(10L);
        itemRequest.setQuantity(2);

        createDTO = new OrderCreateDTO();
        createDTO.setItems(List.of(itemRequest));
    }

    @Test
    void create_Success() {
        when(userService.findByEmail("customer@example.com")).thenReturn(customer);
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(activeItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order created = orderService.create(createDTO, "customer@example.com");

        assertNotNull(created);
        assertEquals(OrderStatus.PENDING, created.getStatus());
        assertEquals(customer, created.getCustomer());
        assertEquals(new BigDecimal("100.00"), created.getTotalPrice());
        assertFalse(created.isPaid());
        assertEquals(1, created.getItems().size());
        assertEquals(activeItem, created.getItems().get(0).getMenuItem());
        assertEquals(2, created.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("50.00"), created.getItems().get(0).getUnitPrice());
    }

    @Test
    void create_ThrowsException_WhenItemNotFound() {
        createDTO.getItems().get(0).setMenuItemId(99L);
        when(userService.findByEmail("customer@example.com")).thenReturn(customer);
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.create(createDTO, "customer@example.com"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void create_ThrowsException_WhenItemInactive() {
        createDTO.getItems().get(0).setMenuItemId(11L);
        when(userService.findByEmail("customer@example.com")).thenReturn(customer);
        when(menuItemRepository.findById(11L)).thenReturn(Optional.of(inactiveItem));

        assertThrows(IllegalArgumentException.class, () -> orderService.create(createDTO, "customer@example.com"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateStatus_ValidTransitions() {
        Order order = new Order();
        order.setId(100L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // PENDING -> PREPARING
        Order updated = orderService.updateStatus(100L, OrderStatus.PREPARING);
        assertEquals(OrderStatus.PREPARING, updated.getStatus());

        // PREPARING -> DELIVERED
        when(orderRepository.findById(100L)).thenReturn(Optional.of(updated));
        Order updated2 = orderService.updateStatus(100L, OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, updated2.getStatus());
    }

    @Test
    void updateStatus_InvalidTransitions() {
        Order order = new Order();
        order.setId(100L);
        order.setStatus(OrderStatus.DELIVERED); // Terminal state

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        // DELIVERED -> PENDING (Invalid)
        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(100L, OrderStatus.PENDING));

        order.setStatus(OrderStatus.CANCELLED); // Terminal state
        // CANCELLED -> PREPARING (Invalid)
        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(100L, OrderStatus.PREPARING));
    }
}
