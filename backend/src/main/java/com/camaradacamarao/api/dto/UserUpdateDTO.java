package com.camaradacamarao.api.dto;

import com.camaradacamarao.api.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDTO {

    @NotBlank
    private String name;

    private String phone;

    private LocalDate birthDate;

    private Gender gender;
}
