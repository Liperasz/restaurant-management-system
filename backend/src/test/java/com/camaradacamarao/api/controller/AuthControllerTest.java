package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.UserRegistrationDTO;
import com.camaradacamarao.api.model.enums.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    void registerAndLogin_Success() throws Exception {
        UserRegistrationDTO registerDTO = new UserRegistrationDTO();
        registerDTO.setName("Integrado Cliente");
        registerDTO.setEmail("cliente.teste@example.com");
        registerDTO.setCpf("99999999991");
        registerDTO.setPhone("11999999999");
        registerDTO.setPassword("SenhaForte123");
        registerDTO.setBirthDate(LocalDate.now().minusYears(25));
        registerDTO.setGender(Gender.MALE);

        // 1. Register user
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("cliente.teste@example.com")))
                .andExpect(jsonPath("$.role", is("CUSTOMER")));

        // 2. Login with registered credentials
        Map<String, String> loginRequest = Map.of(
                "email", "cliente.teste@example.com",
                "password", "SenhaForte123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("CUSTOMER")))
                .andExpect(jsonPath("$.email", is("cliente.teste@example.com")))
                .andReturn();

        // Extract token
        String jsonResponse = loginResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        String token = (String) responseMap.get("token");

        // 3. Request protected "/api/users/me" profile using token
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("cliente.teste@example.com")))
                .andExpect(jsonPath("$.role", is("CUSTOMER")));
    }

    @Test
    void login_Failure_InvalidCredentials() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "email", "cliente.teste@example.com",
                "password", "WrongPassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Credenciais inválidas.")))
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    void protectedProfile_Failure_NoToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden()); // or isUnauthorized() depending on spring security config
    }
}
