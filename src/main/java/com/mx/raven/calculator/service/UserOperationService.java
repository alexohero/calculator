package com.mx.raven.calculator.service;

import com.mx.raven.calculator.model.dto.UserOperationDTO;

import java.math.BigDecimal;

public interface UserOperationService {

    UserOperationDTO calculate(String operation, BigDecimal operandA, BigDecimal operandB, String token);
}
