package com.mx.raven.calculator.persistence.repositories;

import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.entities.UserOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface UserOperationEventRepository {

    UserOperationDTO storeSaveUserOperation(UserOperationDTO dto);
    Page<UserOperationDTO> storePageUserOperations(Specification<UserOperation> spec, Pageable pageable);
    Optional<UserOperationDTO> storeGetByIdUserOperation(Long id);
    void storeDeleteUserOperation(Long id);

}
