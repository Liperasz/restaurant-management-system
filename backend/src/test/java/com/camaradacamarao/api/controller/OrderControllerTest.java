package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.dto.OrderCreateDTO;
import com.camaradacamarao.api.dto.OrderItemRequestDTO;
import com.camaradacamarao.api.dto.StatusUpdateDTO;
import com.camaradacamarao.api.model.enums.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private String getLoginToken(String email, String password) throws Exception {
        Map<String, String> credentials = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, Object> body = objectMapper.readValue(response, Map.class);
        return (String) body.get("token");
    }

    @Test
    void testOrderLifecycleAndSecurity() throws Exception {
        // 1. Log in as Customer
        String customerToken = getLoginToken("customer@camaradacamarao.com", "customer");
        assertNotNull(customerToken);

        // 2. Fetch menu to find a valid menu item id
        MvcResult menuResult = mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andReturn();

        String menuJson = menuResult.getResponse().getContentAsString();
        List<Map<String, Object>> menuItems = objectMapper.readValue(menuJson, List.class);
        Number menuItemId = (Number) menuItems.get(0).get("id");

        // 3. Create Order
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        OrderItemRequestDTO itemDTO = new OrderItemRequestDTO();
        itemDTO.setMenuItemId(menuItemId.longValue());
        itemDTO.setQuantity(2);
        orderCreateDTO.setItems(List.of(itemDTO));

        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.customer.email", is("customer@camaradacamarao.com")))
                .andReturn();

        String orderJson = orderResult.getResponse().getContentAsString();
        Map<String, Object> orderMap = objectMapper.readValue(orderJson, Map.class);
        Number orderId = (Number) orderMap.get("id");

        // 4. View mine orders as Customer
        mockMvc.perform(get("/api/orders/mine")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].id", is(orderId.intValue())));

        // 5. Attempt to list active orders as Customer (should be Forbidden - 403)
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        // 6. Log in as Attendant
        String attendantToken = getLoginToken("attendant@camaradacamarao.com", "attendant");
        assertNotNull(attendantToken);

        // 7. List active orders as Attendant
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + attendantToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].id", hasItem(orderId.intValue())));

        // 8. Update Order Status as Attendant: PENDING -> PREPARING
        StatusUpdateDTO statusUpdate = new StatusUpdateDTO();
        statusUpdate.setStatus(OrderStatus.PREPARING);

        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + attendantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PREPARING")));
    }
}
