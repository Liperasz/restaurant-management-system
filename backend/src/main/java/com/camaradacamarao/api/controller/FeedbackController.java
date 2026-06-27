package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.FeedbackDTO;
import com.camaradacamarao.api.model.Feedback;
import com.camaradacamarao.api.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Feedback submitFeedback(@RequestBody @Valid FeedbackDTO dto, Authentication authentication) {
        return feedbackService.submit(dto, authentication.getName());
    }

    @GetMapping
    public List<Feedback> listFeedbacks() {
        return feedbackService.findAll();
    }
}
