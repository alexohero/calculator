package com.mx.raven.calculator.mappers;

import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.entities.UserOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserOperationsMapper {

    UserOperationsMapper INSTANCE = Mappers.getMapper(UserOperationsMapper.class);

    @Mapping(source = "user.id", target = "userId")
    UserOperationDTO userOperationToUserOperationDTO(UserOperation userOperation);

    @Mapping(source = "userId", target = "user.id")
    UserOperation userOperationDTOToUserOperation(UserOperationDTO userOperationDTO);
}
