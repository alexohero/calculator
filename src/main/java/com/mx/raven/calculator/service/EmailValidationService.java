package com.mx.raven.calculator.service;

import com.mx.raven.calculator.model.EmailValidationResponse;

/**
 * Service for validating email addresses using an external API
 */
public interface EmailValidationService {
    
    /**
     * Validates an email address using the external API
     * 
     * @param email The email address to validate
     * @return The validation response from the API
     */
    EmailValidationResponse validateEmail(String email);
    
    /**
     * Checks if an email is valid according to our criteria:
     * - Has valid format
     * - Has valid MX records
     * - Is not disposable
     * 
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    boolean isEmailValid(String email);
}