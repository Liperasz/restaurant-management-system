package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.UserRegistrationDTO;
import com.camaradacamarao.api.dto.UserUpdateDTO;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.Role;
import com.camaradacamarao.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Valid UserRegistrationDTO dto) {
        return userService.register(dto);
    }

    @GetMapping("/me")
    public User getMe(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @GetMapping("/attendants")
    public List<User> listAttendants() {
        return userService.findAllByRole(Role.ATTENDANT);
    }

    @PostMapping("/attendants")
    @ResponseStatus(HttpStatus.CREATED)
    public User createAttendant(@RequestBody @Valid UserRegistrationDTO dto) {
        return userService.registerAttendant(dto);
    }

    @PutMapping("/attendants/{id}")
    public User updateAttendant(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/attendants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttendant(@PathVariable Long id) {
        userService.delete(id);
    }
}
