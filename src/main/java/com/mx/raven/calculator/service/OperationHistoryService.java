package com.mx.raven.calculator.service;

import com.mx.raven.calculator.model.dto.UserOperationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OperationHistoryService {

    Page<UserOperationDTO> getOperations(Optional<String> operationType, Optional<LocalDateTime> startDate,
                                         Optional<LocalDateTime> endDate, Pageable pageable, String token);
    UserOperationDTO getOperationById(Long id, String token);
    void deleteOperationById(Long id, String token);
}
