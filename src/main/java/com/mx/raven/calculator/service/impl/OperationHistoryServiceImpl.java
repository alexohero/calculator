package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.exceptions.ObjectNotFoundException;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.entities.UserOperation;
import com.mx.raven.calculator.persistence.repositories.UserEventRepository;
import com.mx.raven.calculator.persistence.repositories.UserOperationEventRepository;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.service.OperationHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationHistoryServiceImpl implements OperationHistoryService {
    private static final String OPERATION_FIELD = "operation";
    private static final String TIMESTAMP_FIELD = "timestamp";

    private final UserOperationEventRepository repository;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserEventRepository userEventRepository;

    @Override
    public Page<UserOperationDTO> getOperations(Optional<String> operationType, Optional<LocalDateTime> startDate,
                                                Optional<LocalDateTime> endDate, Pageable pageable, String token) {

        getUserIdFromToken(token);

        Specification<UserOperation> spec = buildSpecification(operationType, startDate, endDate);

        var operations = repository.storePageUserOperations(spec, pageable);
        log.debug("Found {} operations", operations.getTotalElements());
        log.debug("Returning page of {} operations", operations.getNumberOfElements());

        return operations;
    }

    @Override
    public UserOperationDTO getOperationById(Long id, String token) {
        getUserIdFromToken(token);
        log.debug("Getting operation by id: {}", id);

        var operation = repository.storeGetByIdUserOperation(id)
                .orElseThrow(() -> new ObjectNotFoundException("Operation not found with id: " + id));
        log.debug("Found operation: {}", operation);

        return operation;
    }

    @Override
    public void deleteOperationById(Long id, String token) {
        log.debug("Deleting operation by id: {}", id);

        getOperationById(id, token);

        repository.storeDeleteUserOperation(id);
        log.info("Operation deleted successfully");
    }

    private Specification<UserOperation> buildSpecification(Optional<String> operationType,
                                                            Optional<LocalDateTime> startDate,
                                                            Optional<LocalDateTime> endDate) {
        return operationType
                .map(type -> Specification.<UserOperation>where((root, query, cb) ->
                        cb.equal(root.get(OPERATION_FIELD), type)))
                .map(spec -> startDate
                        .map(date -> spec.and((root, query, cb) ->
                                cb.greaterThanOrEqualTo(root.get(TIMESTAMP_FIELD), date)))
                        .orElse(spec))
                .map(spec -> endDate
                        .map(date -> spec.and((root, query, cb) ->
                                cb.lessThanOrEqualTo(root.get(TIMESTAMP_FIELD), date)))
                        .orElse(spec))
                .orElse(Specification.where(null));
    }


    private void getUserIdFromToken(String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token);

        userEventRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
