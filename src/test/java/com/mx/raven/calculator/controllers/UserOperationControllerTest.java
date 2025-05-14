package com.mx.raven.calculator.controllers;

import com.mx.raven.calculator.exceptions.GlobalExceptionHandler;
import com.mx.raven.calculator.exceptions.InvalidObjectException;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.service.UserOperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserOperationControllerTest {

    @Mock
    private UserOperationService userOperationService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UserOperationController userOperationController;

    private MockMvc mockMvc;

    private UserOperationDTO userOperationDTO;
    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userOperationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        token = "Bearer valid-token";

        userOperationDTO = new UserOperationDTO();
        userOperationDTO.setId(1L);
        userOperationDTO.setOperation("add");
        userOperationDTO.setOperandA(BigDecimal.valueOf(10));
        userOperationDTO.setOperandB(BigDecimal.valueOf(5));
        userOperationDTO.setResult(BigDecimal.valueOf(15));
        userOperationDTO.setTimestamp(LocalDateTime.now());
        userOperationDTO.setUserId(1L);
    }

    @Test
    void calculate_Success() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(userOperationService.calculate(
                eq("add"), 
                eq(BigDecimal.valueOf(10)), 
                eq(BigDecimal.valueOf(5)), 
                eq("valid-token")
        )).thenReturn(userOperationDTO);

        mockMvc.perform(post("/calculate")
                .param("operation", "add")
                .param("operandA", "10")
                .param("operandB", "5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.operation", is("add")))
                .andExpect(jsonPath("$.result", is(15)));
    }

    @Test
    void calculate_InvalidOperation() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(userOperationService.calculate(
                eq("invalid"), 
                eq(BigDecimal.valueOf(10)), 
                eq(BigDecimal.valueOf(5)), 
                eq("valid-token")
        )).thenThrow(new InvalidObjectException("Invalid operation: invalid"));

        mockMvc.perform(post("/calculate")
                .param("operation", "invalid")
                .param("operandA", "10")
                .param("operandB", "5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculate_UserNotFound() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(userOperationService.calculate(
                eq("add"), 
                eq(BigDecimal.valueOf(10)), 
                eq(BigDecimal.valueOf(5)), 
                eq("valid-token")
        )).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/calculate")
                .param("operation", "add")
                .param("operandA", "10")
                .param("operandB", "5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void calculate_MissingAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/calculate")
                .param("operation", "add")
                .param("operandA", "10")
                .param("operandB", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
