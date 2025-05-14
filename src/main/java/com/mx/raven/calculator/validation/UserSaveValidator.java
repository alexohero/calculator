package com.mx.raven.calculator.validation;

import com.mx.raven.calculator.exceptions.InvalidObjectException;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.service.EmailValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSaveValidator extends CommonServiceValidator {

    private final EmailValidationService emailValidationService;

    @Override
    public void validate(UserDTO dto) {
        if (dto == null)
            throw new InvalidObjectException("Request to register cannot be null");
        if (StringUtils.isBlank(dto.getUsername()) || !validaCadena(dto.getUsername()))
            throw new InvalidObjectException("Username is required, no special characters and no blanks");
        if (StringUtils.isBlank(dto.getPassword()))
            throw new InvalidObjectException("Password is required");
        if (StringUtils.isBlank(dto.getEmail()))
            throw new InvalidObjectException("Email is required");

        if (!emailValidationService.isEmailValid(dto.getEmail())) {
            log.warn("Invalid email: {}", dto.getEmail());
            throw new InvalidObjectException("Email is invalid. It must have valid format, valid MX records, and not be disposable.");
        }
    }

}
