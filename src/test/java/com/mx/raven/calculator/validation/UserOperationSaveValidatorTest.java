package com.mx.raven.calculator.validation;

import com.mx.raven.calculator.exceptions.InvalidObjectException;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserOperationSaveValidatorTest {

    private UserOperationSaveValidator validator;
    private UserOperationDTO validOperationDTO;

    @BeforeEach
    void setUp() {
        validator = new UserOperationSaveValidator();
        
        validOperationDTO = new UserOperationDTO();
        validOperationDTO.setOperation("add");
        validOperationDTO.setOperandA(BigDecimal.valueOf(10));
        validOperationDTO.setOperandB(BigDecimal.valueOf(5));
    }

    @Test
    void validate_ValidOperation_NoExceptions() {
        assertDoesNotThrow(() -> validator.validate(validOperationDTO));
    }

    @Test
    void validate_NullDTO_ThrowsException() {
        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate((UserOperationDTO) null);
        });

        assertEquals("UserOperationDTO cannot be null", exception.getMessage());
    }

    @Test
    void validate_EmptyOperation_ThrowsException() {
        validOperationDTO.setOperation("");

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("Operation cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"add", "subtract", "multiply", "divide", "sqrt"})
    void validate_ValidOperations_NoExceptions(String operation) {
        validOperationDTO.setOperation(operation);

        assertDoesNotThrow(() -> validator.validate(validOperationDTO));
    }

    @Test
    void validate_InvalidOperation_ThrowsException() {
        validOperationDTO.setOperation("invalid");

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("Invalid operation: invalid", exception.getMessage());
    }

    @Test
    void validate_NullOperandA_ThrowsException() {
        validOperationDTO.setOperandA(null);

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("OperandA cannot be null", exception.getMessage());
    }

    @Test
    void validate_OperandATooSmall_ThrowsException() {
        validOperationDTO.setOperandA(BigDecimal.valueOf(-2000000));

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("OperandA must be between -1000000 and 1000000", exception.getMessage());
    }

    @Test
    void validate_OperandATooLarge_ThrowsException() {
        validOperationDTO.setOperandA(BigDecimal.valueOf(2000000));

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("OperandA must be between -1000000 and 1000000", exception.getMessage());
    }

    @Test
    void validate_NegativeOperandAWithSqrt_ThrowsException() {
        validOperationDTO.setOperation("sqrt");
        validOperationDTO.setOperandA(BigDecimal.valueOf(-10));

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("Square root of negative number is not allowed", exception.getMessage());
    }

    @Test
    void validate_NullOperandB_ThrowsException() {
        validOperationDTO.setOperandB(null);

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("OperandB cannot be null", exception.getMessage());
    }

    @Test
    void validate_OperandBTooSmall_ThrowsException() {
        validOperationDTO.setOperandB(BigDecimal.valueOf(-2000000));

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("OperandB must be between -1000000 and 1000000", exception.getMessage());
    }

    @Test
    void validate_OperandBTooLarge_ThrowsException() {
        validOperationDTO.setOperandB(BigDecimal.valueOf(2000000));

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("OperandB must be between -1000000 and 1000000", exception.getMessage());
    }

    @Test
    void validate_ZeroOperandBWithDivide_ThrowsException() {
        validOperationDTO.setOperation("divide");
        validOperationDTO.setOperandB(BigDecimal.ZERO);

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validOperationDTO);
        });

        assertEquals("Division by zero is not allowed", exception.getMessage());
    }
}