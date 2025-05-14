package com.mx.raven.calculator.validation;

import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.model.dto.UserOperationDTO;

public abstract class CommonServiceValidator {

    public void validate(UserOperationDTO dto) {}
    public void validate(UserDTO dto) {}

    public boolean validaCadena(String input) {
        String regex = "^[a-zA-Z0-9]+$";
        return input.matches(regex);
    }

}
