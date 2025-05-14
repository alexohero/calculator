package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.exceptions.ObjectNotFoundException;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.persistence.repositories.UserEventRepository;
import com.mx.raven.calculator.persistence.repositories.UserOperationEventRepository;
import com.mx.raven.calculator.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationHistoryServiceImplTest {

    @Mock
    private UserOperationEventRepository repository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserEventRepository userEventRepository;

    @InjectMocks
    private OperationHistoryServiceImpl operationHistoryService;

    private String token;
    private UserDTO userDTO;
    private UserOperationDTO userOperationDTO;
    private Page<UserOperationDTO> operationPage;
    private Pageable pageable;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        token = "valid-token";
        
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        
        userOperationDTO = new UserOperationDTO();
        userOperationDTO.setId(1L);
        userOperationDTO.setOperation("add");
        userOperationDTO.setOperandA(BigDecimal.valueOf(10));
        userOperationDTO.setOperandB(BigDecimal.valueOf(5));
        userOperationDTO.setResult(BigDecimal.valueOf(15));
        userOperationDTO.setTimestamp(LocalDateTime.now());
        userOperationDTO.setUserId(1L);
        
        operationPage = new PageImpl<>(List.of(userOperationDTO));
        pageable = PageRequest.of(0, 10);
        
        startDate = LocalDateTime.now().minusDays(7);
        endDate = LocalDateTime.now();
    }

    @Test
    void getOperations_Success() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.of(userDTO));
        when(repository.storePageUserOperations(any(Specification.class), any(Pageable.class))).thenReturn(operationPage);

        Page<UserOperationDTO> result = operationHistoryService.getOperations(
                Optional.of("add"), Optional.of(startDate), Optional.of(endDate), pageable, token);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userOperationDTO.getId(), result.getContent().get(0).getId());
        assertEquals(userOperationDTO.getOperation(), result.getContent().get(0).getOperation());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository).storePageUserOperations(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getOperations_UserNotFound() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            operationHistoryService.getOperations(
                    Optional.of("add"), Optional.of(startDate), Optional.of(endDate), pageable, token);
        });
        
        assertEquals("User not found with username: testuser", exception.getMessage());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository, never()).storePageUserOperations(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getOperationById_Success() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.of(userDTO));
        when(repository.storeGetByIdUserOperation(1L)).thenReturn(Optional.of(userOperationDTO));

        UserOperationDTO result = operationHistoryService.getOperationById(1L, token);

        assertNotNull(result);
        assertEquals(userOperationDTO.getId(), result.getId());
        assertEquals(userOperationDTO.getOperation(), result.getOperation());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository).storeGetByIdUserOperation(1L);
    }

    @Test
    void getOperationById_UserNotFound() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            operationHistoryService.getOperationById(1L, token);
        });
        
        assertEquals("User not found with username: testuser", exception.getMessage());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository, never()).storeGetByIdUserOperation(anyLong());
    }

    @Test
    void getOperationById_OperationNotFound() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.of(userDTO));
        when(repository.storeGetByIdUserOperation(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            operationHistoryService.getOperationById(1L, token);
        });
        
        assertEquals("Operation not found with id: 1", exception.getMessage());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository).storeGetByIdUserOperation(1L);
    }

    @Test
    void deleteOperationById_Success() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.of(userDTO));
        when(repository.storeGetByIdUserOperation(1L)).thenReturn(Optional.of(userOperationDTO));
        doNothing().when(repository).storeDeleteUserOperation(1L);

        operationHistoryService.deleteOperationById(1L, token);

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository).storeGetByIdUserOperation(1L);
        verify(repository).storeDeleteUserOperation(1L);
    }

    @Test
    void deleteOperationById_UserNotFound() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            operationHistoryService.deleteOperationById(1L, token);
        });
        
        assertEquals("User not found with username: testuser", exception.getMessage());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository, never()).storeGetByIdUserOperation(anyLong());
        verify(repository, never()).storeDeleteUserOperation(anyLong());
    }

    @Test
    void deleteOperationById_OperationNotFound() {
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userEventRepository.findByUsername("testuser")).thenReturn(Optional.of(userDTO));
        when(repository.storeGetByIdUserOperation(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            operationHistoryService.deleteOperationById(1L, token);
        });
        
        assertEquals("Operation not found with id: 1", exception.getMessage());

        verify(jwtTokenUtil).getUsernameFromToken(token);
        verify(userEventRepository).findByUsername("testuser");
        verify(repository).storeGetByIdUserOperation(1L);
        verify(repository, never()).storeDeleteUserOperation(anyLong());
    }
}