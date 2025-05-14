package com.mx.raven.calculator.persistence.repositories.impl;

import com.mx.raven.calculator.mappers.UserOperationsMapperService;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.entities.UserOperation;
import com.mx.raven.calculator.persistence.repositories.UserOperationEventRepository;
import com.mx.raven.calculator.persistence.stores.UserOperationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserOperationEventRepositoryImpl implements UserOperationEventRepository {

    private final UserOperationStore userOperationStore;
    private final UserOperationsMapperService mapper;

    @Override
    public UserOperationDTO storeSaveUserOperation(UserOperationDTO dto) {
        var entity = userOperationStore.save(mapper.userOperationDTOToUserOperation(dto));
        return mapper.userOperationToUserOperationDTO(entity);
    }

    @Override
    public Page<UserOperationDTO> storePageUserOperations(Specification<UserOperation> spec, Pageable pageable) {
        var operations = userOperationStore.findAll(spec, pageable);
        return operations.map(mapper::userOperationToUserOperationDTO);
    }

    @Override
    public Optional<UserOperationDTO> storeGetByIdUserOperation(Long id) {
        var operation = userOperationStore.findById(id);
        return operation.map(mapper::userOperationToUserOperationDTO);
    }

    @Override
    @Transactional
    public void storeDeleteUserOperation(Long id) {
        userOperationStore.deleteById(id);
    }
}
