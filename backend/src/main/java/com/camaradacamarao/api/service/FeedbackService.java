package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.FeedbackDTO;
import com.camaradacamarao.api.model.Feedback;
import com.camaradacamarao.api.model.Order;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.OrderStatus;
import com.camaradacamarao.api.repository.FeedbackRepository;
import com.camaradacamarao.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final OrderRepository orderRepository;
    private final UserService userService; // P06 fix — use service, not repository

    public Feedback submit(FeedbackDTO dto, String customerEmail) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido não encontrado com id: " + dto.getOrderId()));

        // P05 — Feedback only allowed after delivery
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException(
                    "Avaliação só pode ser enviada após a entrega do pedido. " +
                    "Status atual: " + order.getStatus());
        }

        // Verify the order belongs to this customer (authorization check)
        if (!order.getCustomer().getEmail().equals(customerEmail)) {
            throw new IllegalArgumentException(
                    "Este pedido não pertence ao cliente autenticado.");
        }

        User customer = userService.findByEmail(customerEmail); // P06 — via service

        Feedback feedback = new Feedback();
        feedback.setComment(dto.getComment());
        feedback.setRating(dto.getRating());
        feedback.setOrder(order);
        feedback.setUser(customer);

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }
}
