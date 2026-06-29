package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.FeedbackDTO;
import com.camaradacamarao.api.dto.FeedbackResponseDTO;
import com.camaradacamarao.api.model.Feedback;
import com.camaradacamarao.api.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponseDTO submitFeedback(
            @RequestBody @Valid FeedbackDTO dto,
            Authentication authentication) {
        return toDTO(feedbackService.submit(dto, authentication.getName()));
    }

    @GetMapping
    public List<FeedbackResponseDTO> listFeedbacks() {
        return feedbackService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Mapping — Feedbacks.jsx expects: fb.id, fb.rating, fb.order.id,
    //                                  fb.user.name, fb.comment, fb.createdAt
    // We flatten order.id → orderId and user.name → userName,
    // then the frontend will be updated to use these flat paths.
    // -------------------------------------------------------------------------
    private FeedbackResponseDTO toDTO(Feedback fb) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(fb.getId());
        dto.setComment(fb.getComment());
        dto.setRating(fb.getRating());
        dto.setCreatedAt(fb.getCreatedAt());
        if (fb.getOrder() != null) {
            dto.setOrderId(fb.getOrder().getId());
        }
        if (fb.getUser() != null) {
            dto.setUserName(fb.getUser().getName());
        }
        return dto;
    }
}
