package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.FeedbackDTO;
import com.camaradacamarao.api.model.Feedback;
import com.camaradacamarao.api.model.Order;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.repository.FeedbackRepository;
import com.camaradacamarao.api.repository.OrderRepository;
import com.camaradacamarao.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public Feedback submit(FeedbackDTO dto, String customerEmail) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + dto.getOrderId()));

        if (!order.getCustomer().getEmail().equals(customerEmail)) {
            throw new IllegalArgumentException("Customer did not place this order");
        }

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

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
