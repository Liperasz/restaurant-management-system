package com.camaradacamarao.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackDTO {

    @NotBlank
    @Size(max = 500)
    private String comment;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotNull
    private Long orderId;
}
