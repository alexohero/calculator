package com.mx.raven.calculator.validation;

import com.mx.raven.calculator.exceptions.InvalidObjectException;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserOperationSaveValidator extends CommonServiceValidator {

    private static final BigDecimal MIN_VALUE = new BigDecimal("-1000000");
    private static final BigDecimal MAX_VALUE = new BigDecimal("1000000");

    @Override
    public void validate(UserOperationDTO dto) {
        if (dto == null)
            throw new InvalidObjectException("UserOperationDTO cannot be null");
        if (StringUtils.isBlank(dto.getOperation()))
            throw new InvalidObjectException("Operation cannot be null or empty");

        String operation = dto.getOperation().toLowerCase();
        if (!operation.equals("add") && !operation.equals("subtract") && 
            !operation.equals("multiply") && !operation.equals("divide") && 
            !operation.equals("sqrt"))
            throw new InvalidObjectException("Invalid operation: " + dto.getOperation());

        if (dto.getOperandA() == null)
            throw new InvalidObjectException("OperandA cannot be null");
        if (dto.getOperandA().compareTo(MIN_VALUE) < 0 || dto.getOperandA().compareTo(MAX_VALUE) > 0)
            throw new InvalidObjectException("OperandA must be between -1000000 and 1000000");
        if (operation.equals("sqrt") && dto.getOperandA().compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidObjectException("Square root of negative number is not allowed");
        if (dto.getOperandB() == null)
            throw new InvalidObjectException("OperandB cannot be null");
        if (dto.getOperandB().compareTo(MIN_VALUE) < 0 || dto.getOperandB().compareTo(MAX_VALUE) > 0)
            throw new InvalidObjectException("OperandB must be between -1000000 and 1000000");
        if (operation.equals("divide") && dto.getOperandB().compareTo(BigDecimal.ZERO) == 0)
            throw new InvalidObjectException("Division by zero is not allowed");

    }
}
