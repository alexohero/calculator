package com.mx.raven.calculator.validation;

import com.mx.raven.calculator.exceptions.InvalidObjectException;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.service.EmailValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSaveValidatorTest {

    @Mock
    private EmailValidationService emailValidationService;

    private UserSaveValidator validator;
    private UserDTO validUserDTO;

    @BeforeEach
    void setUp() {
        validator = new UserSaveValidator(emailValidationService);

        validUserDTO = new UserDTO();
        validUserDTO.setUsername("testuser");
        validUserDTO.setPassword("password123");
        validUserDTO.setEmail("test@example.com");
    }

    @Test
    void validate_ValidUser_NoExceptions() {
        when(emailValidationService.isEmailValid(validUserDTO.getEmail())).thenReturn(true);

        assertDoesNotThrow(() -> validator.validate(validUserDTO));

        verify(emailValidationService).isEmailValid(validUserDTO.getEmail());
    }

    @Test
    void validate_NullUser_ThrowsException() {
        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate((UserDTO) null);
        });

        assertEquals("Request to register cannot be null", exception.getMessage());
    }

    @Test
    void validate_EmptyUsername_ThrowsException() {
        validUserDTO.setUsername("");

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validUserDTO);
        });

        assertEquals("Username is required, no special characters and no blanks", exception.getMessage());
    }

    @Test
    void validate_UsernameWithSpecialChars_ThrowsException() {
        validUserDTO.setUsername("test@user");

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validUserDTO);
        });

        assertEquals("Username is required, no special characters and no blanks", exception.getMessage());
    }

    @Test
    void validate_NullPassword_ThrowsException() {
        validUserDTO.setPassword(null);

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validUserDTO);
        });

        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    void validate_EmptyPassword_ThrowsException() {
        validUserDTO.setPassword("");

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validUserDTO);
        });

        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    void validate_NullEmail_ThrowsException() {
        validUserDTO.setEmail(null);

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validUserDTO);
        });

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void validate_EmptyEmail_ThrowsException() {
        validUserDTO.setEmail("");

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validUserDTO);
        });

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void validate_InvalidEmail_ThrowsException() {
        String invalidEmail = "invalid@example.com";
        validUserDTO.setEmail(invalidEmail);

        when(emailValidationService.isEmailValid(invalidEmail)).thenReturn(false);

        Exception exception = assertThrows(InvalidObjectException.class, () -> {
            validator.validate(validUserDTO);
        });

        assertEquals("Email is invalid. It must have valid format, valid MX records, and not be disposable.", exception.getMessage());
        verify(emailValidationService).isEmailValid(invalidEmail);
    }

    @Test
    void validate_ValidEmail_CallsEmailValidationService() {
        String validEmail = "valid@example.com";
        validUserDTO.setEmail(validEmail);

        when(emailValidationService.isEmailValid(validEmail)).thenReturn(true);

        validator.validate(validUserDTO);

        verify(emailValidationService).isEmailValid(validEmail);
    }
}
