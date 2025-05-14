package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.exceptions.InvalidObjectException;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.repositories.UserEventRepository;
import com.mx.raven.calculator.persistence.repositories.UserOperationEventRepository;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.validation.UserOperationSaveValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserOperationServiceImplTest {

    @Mock
    private UserOperationSaveValidator validator;

    @Mock
    private UserOperationEventRepository repository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserEventRepository userEventRepository;

    @InjectMocks
    private UserOperationServiceImpl userOperationService;

    private String token;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        token = "valid-token";

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
    }

    @ParameterizedTest
    @CsvSource({
        "add, 10, 5, 15.0",
        "subtract, 10, 5, 5.0",
        "multiply, 10, 5, 50.0",
        "divide, 10, 5, 2.0",
        "sqrt, 25, 0, 5.0"
    })
    void calculate_ValidOperations(String operation, double operandA, double operandB, double expectedResult) {
        BigDecimal a = BigDecimal.valueOf(operandA);
        BigDecimal b = BigDecimal.valueOf(operandB);

        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.of(userDTO));
        doNothing().when(validator).validate(any(UserOperationDTO.class));

        UserOperationDTO savedOperation = new UserOperationDTO();
        savedOperation.setId(1L);
        savedOperation.setOperation(operation);
        savedOperation.setOperandA(a);
        savedOperation.setOperandB(b);
        savedOperation.setResult(BigDecimal.valueOf(expectedResult));
        savedOperation.setTimestamp(LocalDateTime.now());
        savedOperation.setUserId(1L);

        when(repository.storeSaveUserOperation(any(UserOperationDTO.class))).thenReturn(savedOperation);

        UserOperationDTO result = userOperationService.calculate(operation, a, b, token);

        assertNotNull(result);
        assertEquals(operation, result.getOperation());
        assertEquals(a, result.getOperandA());
        assertEquals(b, result.getOperandB());
        assertEquals(BigDecimal.valueOf(expectedResult).setScale(1, RoundingMode.HALF_UP), result.getResult());
        assertEquals(1L, result.getUserId());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(validator).validate(any(UserOperationDTO.class));
        verify(repository).storeSaveUserOperation(any(UserOperationDTO.class));
    }

    @Test
    void calculate_InvalidOperation() {
        String operation = "invalid";
        BigDecimal a = BigDecimal.valueOf(10);
        BigDecimal b = BigDecimal.valueOf(5);

        doNothing().when(validator).validate(any(UserOperationDTO.class));

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            userOperationService.calculate(operation, a, b, token);
        });

        assertEquals("Invalid operation: invalid", exception.getMessage());

        verify(validator).validate(any(UserOperationDTO.class));
        verify(repository, never()).storeSaveUserOperation(any(UserOperationDTO.class));
    }

    @Test
    void calculate_DivideByZero() {
        String operation = "divide";
        BigDecimal a = BigDecimal.valueOf(10);
        BigDecimal b = BigDecimal.ZERO;

        doNothing().when(validator).validate(any(UserOperationDTO.class));

        assertThrows(ArithmeticException.class, () -> {
            userOperationService.calculate(operation, a, b, token);
        });

        verify(validator).validate(any(UserOperationDTO.class));
        verify(repository, never()).storeSaveUserOperation(any(UserOperationDTO.class));
    }

    @Test
    void calculate_ValidationFails() {
        String operation = "add";
        BigDecimal a = BigDecimal.valueOf(10);
        BigDecimal b = BigDecimal.valueOf(5);

        doThrow(new IllegalArgumentException("Validation failed")).when(validator).validate(any(UserOperationDTO.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userOperationService.calculate(operation, a, b, token);
        });

        assertEquals("Validation failed", exception.getMessage());

        verify(validator).validate(any(UserOperationDTO.class));
        verify(repository, never()).storeSaveUserOperation(any(UserOperationDTO.class));
    }

    @Test
    void calculate_UserNotFound() {
        String operation = "add";
        BigDecimal a = BigDecimal.valueOf(10);
        BigDecimal b = BigDecimal.valueOf(5);

        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        doNothing().when(validator).validate(any(UserOperationDTO.class));

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userOperationService.calculate(operation, a, b, token);
        });

        assertEquals("User not found with username: testuser", exception.getMessage());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(validator).validate(any(UserOperationDTO.class));
        verify(repository, never()).storeSaveUserOperation(any(UserOperationDTO.class));
    }
}
