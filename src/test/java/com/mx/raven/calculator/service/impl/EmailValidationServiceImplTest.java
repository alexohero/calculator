package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.model.EmailValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailValidationServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmailValidationServiceImpl emailValidationService;

    private final String testApiKey = "test-api-key";
    private final String testApiUrl = "http://test-api-url";
    private final String validEmail = "valid@example.com";
    private final String invalidEmail = "invalid@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailValidationService, "apiKey", testApiKey);
        ReflectionTestUtils.setField(emailValidationService, "apiUrl", testApiUrl);
    }

    @Test
    void validateEmail_ValidEmail_ReturnsValidResponse() {
        EmailValidationResponse validResponse = new EmailValidationResponse();
        validResponse.setEmail(validEmail);
        validResponse.setFormatValid(true);
        validResponse.setMxFound(true);
        validResponse.setDisposable(false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class)))
                .thenReturn(validResponse);

        EmailValidationResponse result = emailValidationService.validateEmail(validEmail);

        assertNotNull(result);
        assertEquals(validEmail, result.getEmail());
        assertTrue(result.isFormatValid());
        assertTrue(result.isMxFound());
        assertFalse(result.isDisposable());

        verify(restTemplate).getForObject(contains(testApiUrl), eq(EmailValidationResponse.class));
        verify(restTemplate).getForObject(contains("access_key=" + testApiKey), eq(EmailValidationResponse.class));
        verify(restTemplate).getForObject(contains("email=" + validEmail), eq(EmailValidationResponse.class));
    }

    @Test
    void validateEmail_ApiError_ReturnsFallbackResponse() {
        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class)))
                .thenThrow(new RuntimeException("API Error"));

        EmailValidationResponse result = emailValidationService.validateEmail(validEmail);

        assertNotNull(result);
        assertEquals(validEmail, result.getEmail());
        assertFalse(result.isFormatValid());
        assertFalse(result.isMxFound());
        assertTrue(result.isDisposable());

        verify(restTemplate).getForObject(anyString(), eq(EmailValidationResponse.class));
    }

    @Test
    void isEmailValid_ValidEmail_ReturnsTrue() {
        EmailValidationResponse validResponse = new EmailValidationResponse();
        validResponse.setEmail(validEmail);
        validResponse.setFormatValid(true);
        validResponse.setMxFound(true);
        validResponse.setDisposable(false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class)))
                .thenReturn(validResponse);

        boolean result = emailValidationService.isEmailValid(validEmail);

        assertTrue(result);

        verify(restTemplate).getForObject(anyString(), eq(EmailValidationResponse.class));
    }

    @Test
    void isEmailValid_InvalidFormat_ReturnsFalse() {
        EmailValidationResponse invalidResponse = new EmailValidationResponse();
        invalidResponse.setEmail(invalidEmail);
        invalidResponse.setFormatValid(false);
        invalidResponse.setMxFound(true);
        invalidResponse.setDisposable(false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class)))
                .thenReturn(invalidResponse);

        boolean result = emailValidationService.isEmailValid(invalidEmail);

        assertFalse(result);

        verify(restTemplate).getForObject(anyString(), eq(EmailValidationResponse.class));
    }

    @Test
    void isEmailValid_InvalidMx_ReturnsFalse() {
        EmailValidationResponse invalidResponse = new EmailValidationResponse();
        invalidResponse.setEmail(invalidEmail);
        invalidResponse.setFormatValid(true);
        invalidResponse.setMxFound(false);
        invalidResponse.setDisposable(false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class)))
                .thenReturn(invalidResponse);

        boolean result = emailValidationService.isEmailValid(invalidEmail);

        assertFalse(result);

        verify(restTemplate).getForObject(anyString(), eq(EmailValidationResponse.class));
    }

    @Test
    void isEmailValid_DisposableEmail_ReturnsFalse() {
        EmailValidationResponse invalidResponse = new EmailValidationResponse();
        invalidResponse.setEmail(invalidEmail);
        invalidResponse.setFormatValid(true);
        invalidResponse.setMxFound(true);
        invalidResponse.setDisposable(true);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class)))
                .thenReturn(invalidResponse);

        boolean result = emailValidationService.isEmailValid(invalidEmail);

        assertFalse(result);

        verify(restTemplate).getForObject(anyString(), eq(EmailValidationResponse.class));
    }
}