package com.camaradacamarao.api.dto;

import com.camaradacamarao.api.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;

    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve conter 10 ou 11 dígitos numéricos")
    private String phone;

    @NotBlank(message = "Senha é obrigatória")
    @Pattern(
        regexp = "^(?=.*[A-Z]).{6,}$",
        message = "Senha deve ter no mínimo 6 caracteres e ao menos uma letra maiúscula"
    )
    private String password;

    private LocalDate birthDate;

    private Gender gender;
}
