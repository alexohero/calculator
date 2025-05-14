package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.exceptions.InvalidObjectException;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.repositories.UserEventRepository;
import com.mx.raven.calculator.persistence.repositories.UserOperationEventRepository;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.service.UserOperationService;
import com.mx.raven.calculator.validation.UserOperationSaveValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserOperationServiceImpl implements UserOperationService {

    private final UserOperationSaveValidator validator;
    private final UserOperationEventRepository repository;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserEventRepository userEventRepository;

    @Override
    public UserOperationDTO calculate(String operation, BigDecimal operandA, BigDecimal operandB, String token) {
        var requestDto = new UserOperationDTO(null, operation, operandA, operandB, null, null, null);
        return calculate(requestDto, token);
    }

    private UserOperationDTO calculate(UserOperationDTO requestDto, String token) {

        validator.validate(requestDto);
        log.debug("Valid operation.");

        BigDecimal result = performCalculation(requestDto.getOperation(),
                requestDto.getOperandA(),
                requestDto.getOperandB());

        requestDto.setResult(result);
        requestDto.setTimestamp(LocalDateTime.now());
        requestDto.setUserId(getUserIdFromUsername(jwtTokenUtil.getUsernameFromToken(token)));

        return saveOperation(requestDto);
    }

    private UserOperationDTO saveOperation(UserOperationDTO requestDto) {
        var responseDto = repository.storeSaveUserOperation(requestDto);
        log.info("UserOperationDTO saved: {}", responseDto);

        if (requestDto.getUserId() != null) {
            responseDto.setUserId(requestDto.getUserId());
        }

        log.info("Operation saved successfully");
        log.debug("Response DTO: {}", responseDto);

        return responseDto;
    }

    private BigDecimal performCalculation(String operation, BigDecimal operandA, BigDecimal operandB) {
        BigDecimal result = switch (operation.toLowerCase()) {
            case "add" -> operandA.add(operandB);
            case "subtract" -> operandA.subtract(operandB);
            case "multiply" -> operandA.multiply(operandB);
            case "divide" -> operandA.divide(operandB, 1, RoundingMode.HALF_UP);
            case "sqrt" -> BigDecimal.valueOf(Math.sqrt(operandA.doubleValue())).setScale(1, RoundingMode.HALF_UP);
            default -> throw new InvalidObjectException("Invalid operation: " + operation);
        };

        return result.setScale(1, RoundingMode.HALF_UP);
    }

    private Long getUserIdFromUsername(String username) {
        var user = userEventRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return user.getId();
    }
}
