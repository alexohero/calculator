package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.model.EmailValidationResponse;
import com.mx.raven.calculator.service.EmailValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailValidationServiceImpl implements EmailValidationService {

    private final RestTemplate restTemplate;

    @Value("${mailboxlayer.key}")
    private String apiKey;

    @Value("${mailboxlayer.url-api}")
    private String apiUrl;

    @Override
    public EmailValidationResponse validateEmail(String email) {
        log.info("Validating email: {}", email);
        
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("access_key", apiKey)
                .queryParam("email", email)
                .queryParam("smtp", 1)
                .queryParam("format", 1)
                .build()
                .toUriString();
        
        log.debug("Calling email validation API with URL: {}", url);
        
        try {
            EmailValidationResponse response = restTemplate.getForObject(url, EmailValidationResponse.class);
            log.debug("Email validation response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error validating email: {}", e.getMessage(), e);

            EmailValidationResponse fallbackResponse = new EmailValidationResponse();
            fallbackResponse.setEmail(email);
            fallbackResponse.setFormatValid(false);
            fallbackResponse.setMxFound(false);
            fallbackResponse.setDisposable(true);
            return fallbackResponse;
        }
    }

    @Override
    public boolean isEmailValid(String email) {
        EmailValidationResponse response = validateEmail(email);
        
        boolean isValid = response.isFormatValid() && 
                          response.isMxFound() && 
                          !response.isDisposable();
        
        log.info("Email {} validation result: {}", email, isValid);
        
        return isValid;
    }
}