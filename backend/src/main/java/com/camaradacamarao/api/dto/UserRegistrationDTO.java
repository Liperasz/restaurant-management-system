package com.camaradacamarao.api.dto;

import com.camaradacamarao.api.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 11, max = 11)
    private String cpf;

    private String phone;

    @NotBlank
    private String password;

    private LocalDate birthDate;

    private Gender gender;
}
