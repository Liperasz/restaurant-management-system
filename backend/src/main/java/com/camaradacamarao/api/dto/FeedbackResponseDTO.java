package com.camaradacamarao.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Safe Feedback response — exposes only what the frontend needs.
 *
 * Frontend (Feedbacks.jsx) consumes:
 *   fb.id, fb.rating, fb.order.id, fb.user.name, fb.comment, fb.createdAt
 */
@Data
public class FeedbackResponseDTO {
    private Long id;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;
    private Long orderId;       // fb.order.id
    private String userName;   // fb.user.name
}
