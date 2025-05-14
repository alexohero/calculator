package com.mx.raven.calculator.mappers;

import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.entities.UserOperation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserOperationsMapperService {

    private final UserOperationsMapper userOperationsMapper;

    public UserOperationDTO userOperationToUserOperationDTO(UserOperation userOperation) {
        return userOperationsMapper.userOperationToUserOperationDTO(userOperation);
    }

    public UserOperation userOperationDTOToUserOperation(UserOperationDTO userOperationDTO) {
        return userOperationsMapper.userOperationDTOToUserOperation(userOperationDTO);
    }

    public UserOperationDTO parametersToUserOperationDTO(String operation, BigDecimal operandA, BigDecimal operandB) {
        return new UserOperationDTO(null, operation, operandA, operandB, null, null, null);
    }

}
