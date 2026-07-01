package com.camaradacamarao.api.service;

import com.camaradacamarao.api.dto.UserRegistrationDTO;
import com.camaradacamarao.api.dto.UserUpdateDTO;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.Gender;
import com.camaradacamarao.api.model.enums.Role;
import com.camaradacamarao.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDTO regDTO;
    private User user;

    @BeforeEach
    void setUp() {
        regDTO = new UserRegistrationDTO();
        regDTO.setName("Test User");
        regDTO.setEmail("test@example.com");
        regDTO.setCpf("12345678901");
        regDTO.setPhone("11999999999");
        regDTO.setPassword("Password123!");
        regDTO.setBirthDate(LocalDate.now().minusYears(20)); // age 20
        regDTO.setGender(Gender.OTHER);

        user = new User();
        user.setId(1L);
        user.setName(regDTO.getName());
        user.setEmail(regDTO.getEmail());
        user.setCpf(regDTO.getCpf());
        user.setPhone(regDTO.getPhone());
        user.setPassword("hashedPassword");
        user.setBirthDate(regDTO.getBirthDate());
        user.setGender(regDTO.getGender());
        user.setRole(Role.CUSTOMER);
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(regDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(regDTO.getCpf())).thenReturn(false);
        when(passwordEncoder.encode(regDTO.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.register(regDTO);

        assertNotNull(savedUser);
        assertEquals(Role.CUSTOMER, savedUser.getRole());
        assertEquals("test@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenEmailExists() {
        when(userRepository.existsByEmail(regDTO.getEmail())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(regDTO));
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenCpfExists() {
        when(userRepository.existsByEmail(regDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(regDTO.getCpf())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(regDTO));
        assertEquals("CPF already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenUnder18() {
        regDTO.setBirthDate(LocalDate.now().minusYears(17)); // age 17
        when(userRepository.existsByEmail(regDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(regDTO.getCpf())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(regDTO));
        assertEquals("User must be at least 18 years old", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerAttendant_Success() {
        when(userRepository.existsByEmail(regDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(regDTO.getCpf())).thenReturn(false);
        when(passwordEncoder.encode(regDTO.getPassword())).thenReturn("hashedPassword");
        
        User attendantUser = new User();
        attendantUser.setId(2L);
        attendantUser.setName(regDTO.getName());
        attendantUser.setEmail(regDTO.getEmail());
        attendantUser.setRole(Role.ATTENDANT);
        when(userRepository.save(any(User.class))).thenReturn(attendantUser);

        User savedUser = userService.registerAttendant(regDTO);

        assertNotNull(savedUser);
        assertEquals(Role.ATTENDANT, savedUser.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User found = userService.findByEmail("test@example.com");

        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void findByEmail_ThrowsException_WhenNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findByEmail("notfound@example.com"));
    }

    @Test
    void update_Success() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setPhone("11888888888");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updated = userService.update(1L, updateDTO);

        assertNotNull(updated);
        assertEquals("Updated Name", updated.getName());
        assertEquals("11888888888", updated.getPhone());
    }

    @Test
    void delete_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ThrowsException_WhenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.delete(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
