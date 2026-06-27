package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.UserRegistrationDTO;
import com.camaradacamarao.api.dto.UserUpdateDTO;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.Role;
import com.camaradacamarao.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF already exists");
        }
        if (dto.getBirthDate() != null) {
            int age = Period.between(dto.getBirthDate(), LocalDate.now()).getYears();
            if (age < 18) {
                throw new IllegalArgumentException("User must be at least 18 years old");
            }
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setCpf(dto.getCpf());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public List<User> findAllByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    public User update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());

        return userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
