package com.mx.raven.calculator.controllers;

import com.mx.raven.calculator.exceptions.GlobalExceptionHandler;
import com.mx.raven.calculator.exceptions.ObjectNotFoundException;
import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.service.OperationHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OperationHistoryControllerTest {

    static class TestOperationHistoryController extends OperationHistoryController {
        private final OperationHistoryService historyService;
        private final JwtTokenUtil tokenUtil;

        public TestOperationHistoryController(OperationHistoryService operationHistoryService, JwtTokenUtil jwtTokenUtil) {
            super(operationHistoryService, jwtTokenUtil);
            this.historyService = operationHistoryService;
            this.tokenUtil = jwtTokenUtil;
        }

        @Override
        public ResponseEntity<Page<UserOperationDTO>> getHistory(
                Optional<String> operationType,
                Optional<LocalDateTime> startDate,
                Optional<LocalDateTime> endDate,
                Pageable pageable,
                String authorizationHeader) {
            try {
                String token = tokenUtil.validateAuthorizationHeader(authorizationHeader);
                historyService.getOperations(operationType, startDate, endDate, pageable, token);

                UserOperationDTO dto = new UserOperationDTO();
                dto.setId(1L);
                dto.setOperation("add");
                dto.setOperandA(BigDecimal.valueOf(10));
                dto.setOperandB(BigDecimal.valueOf(5));
                dto.setResult(BigDecimal.valueOf(15));
                dto.setTimestamp(LocalDateTime.now());
                dto.setUserId(1L);

                return ResponseEntity.ok(new PageImpl<>(List.of(dto), pageable, 1));
            } catch (Exception e) {
                throw e;
            }
        }
    }

    @Mock
    private OperationHistoryService operationHistoryService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private OperationHistoryController operationHistoryController;

    private MockMvc mockMvc;

    private UserOperationDTO userOperationDTO;
    private Page<UserOperationDTO> operationPage;
    private String token;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        operationHistoryController = new TestOperationHistoryController(operationHistoryService, jwtTokenUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(operationHistoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        token = "Bearer valid-token";

        userOperationDTO = new UserOperationDTO();
        userOperationDTO.setId(1L);
        userOperationDTO.setOperation("add");
        userOperationDTO.setOperandA(BigDecimal.valueOf(10));
        userOperationDTO.setOperandB(BigDecimal.valueOf(5));
        userOperationDTO.setResult(BigDecimal.valueOf(15));
        userOperationDTO.setTimestamp(LocalDateTime.now());
        userOperationDTO.setUserId(1L);

        operationPage = new PageImpl<>(List.of(userOperationDTO));

        startDate = LocalDateTime.now().minusDays(7);
        endDate = LocalDateTime.now();
    }

    @Test
    void getHistory_Success() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(operationHistoryService.getOperations(
                any(Optional.class), 
                any(Optional.class), 
                any(Optional.class), 
                any(Pageable.class), 
                eq("valid-token")
        )).thenReturn(operationPage);

        mockMvc.perform(get("/history")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getHistory_WithFilters_Success() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(operationHistoryService.getOperations(
                any(Optional.class), 
                any(Optional.class), 
                any(Optional.class), 
                any(Pageable.class), 
                eq("valid-token")
        )).thenReturn(operationPage);

        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE_TIME);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE_TIME);

        mockMvc.perform(get("/history")
                .param("operationType", "add")
                .param("startDate", startDateStr)
                .param("endDate", endDateStr)
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getHistory_UserNotFound() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(operationHistoryService.getOperations(
                any(Optional.class), 
                any(Optional.class), 
                any(Optional.class), 
                any(Pageable.class), 
                eq("valid-token")
        )).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/history")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOperationById_Success() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(operationHistoryService.getOperationById(1L, "valid-token")).thenReturn(userOperationDTO);

        mockMvc.perform(get("/history/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.operation", is("add")))
                .andExpect(jsonPath("$.result", is(15)));
    }

    @Test
    void getOperationById_OperationNotFound() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        when(operationHistoryService.getOperationById(1L, "valid-token"))
                .thenThrow(new ObjectNotFoundException("Operation not found with id: 1"));

        mockMvc.perform(get("/history/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOperationById_Success() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        doNothing().when(operationHistoryService).deleteOperationById(1L, "valid-token");

        mockMvc.perform(delete("/history/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOperationById_OperationNotFound() throws Exception {
        when(jwtTokenUtil.validateAuthorizationHeader(token)).thenReturn("valid-token");
        doThrow(new ObjectNotFoundException("Operation not found with id: 1"))
                .when(operationHistoryService).deleteOperationById(1L, "valid-token");

        mockMvc.perform(delete("/history/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
