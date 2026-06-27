package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.UserRegistrationDTO;
import com.camaradacamarao.api.dto.UserUpdateDTO;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.Role;
import com.camaradacamarao.api.dto.UserResponseDTO;
import com.camaradacamarao.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody @Valid UserRegistrationDTO dto) {
        return toDTO(userService.register(dto));
    }

    @GetMapping("/me")
    public UserResponseDTO getMe(Authentication authentication) {
        return toDTO(userService.findByEmail(authentication.getName()));
    }

    @GetMapping("/attendants")
    public List<UserResponseDTO> listAttendants() {
        return userService.findAllByRole(Role.ATTENDANT).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/attendants")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createAttendant(@RequestBody @Valid UserRegistrationDTO dto) {
        return toDTO(userService.registerAttendant(dto));
    }

    @PutMapping("/attendants/{id}")
    public UserResponseDTO updateAttendant(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto) {
        return toDTO(userService.update(id, dto));
    }

    @DeleteMapping("/attendants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttendant(@PathVariable Long id) {
        userService.delete(id);
    }

    private UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCpf(user.getCpf());
        dto.setPhone(user.getPhone());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole());
        return dto;
    }
}
