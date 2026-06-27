package com.camaradacamarao.api.dto;

import com.camaradacamarao.api.model.enums.Gender;
import com.camaradacamarao.api.model.enums.Role;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
    private Role role;
}
