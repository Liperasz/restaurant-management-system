package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.FeedbackDTO;
import com.camaradacamarao.api.model.Feedback;
import com.camaradacamarao.api.model.Order;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.OrderStatus;
import com.camaradacamarao.api.repository.FeedbackRepository;
import com.camaradacamarao.api.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FeedbackService feedbackService;

    private User customer;
    private Order order;
    private FeedbackDTO feedbackDTO;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@example.com");

        order = new Order();
        order.setId(200L);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.DELIVERED);

        feedbackDTO = new FeedbackDTO();
        feedbackDTO.setOrderId(200L);
        feedbackDTO.setComment("Comida muito gostosa!");
        feedbackDTO.setRating(5);
    }

    @Test
    void submit_Success() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order));
        when(userService.findByEmail("customer@example.com")).thenReturn(customer);
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback f = invocation.getArgument(0);
            f.setId(10L);
            return f;
        });

        Feedback saved = feedbackService.submit(feedbackDTO, "customer@example.com");

        assertNotNull(saved);
        assertEquals(10L, saved.getId());
        assertEquals("Comida muito gostosa!", saved.getComment());
        assertEquals(5, saved.getRating());
        assertEquals(order, saved.getOrder());
        assertEquals(customer, saved.getUser());
    }

    @Test
    void submit_ThrowsException_WhenOrderNotFound() {
        when(orderRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> feedbackService.submit(feedbackDTO, "customer@example.com"));
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void submit_ThrowsException_WhenOrderNotDelivered() {
        order.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> feedbackService.submit(feedbackDTO, "customer@example.com"));
        assertTrue(exception.getMessage().contains("Avaliação só pode ser enviada após a entrega"));
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void submit_ThrowsException_WhenOrderBelongsToOtherCustomer() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> feedbackService.submit(feedbackDTO, "other_customer@example.com"));
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }
}
