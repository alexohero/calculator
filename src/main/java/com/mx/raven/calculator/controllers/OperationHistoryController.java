package com.mx.raven.calculator.controllers;

import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.service.OperationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(path = "history")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Operation History", description = "API for managing calculation history")
public class OperationHistoryController {

    private final OperationHistoryService operationHistoryService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(
        summary = "Get operation history", 
        description = "Retrieves the history of calculations with optional filtering and pagination",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved operation history")
    })
    @GetMapping
    public ResponseEntity<Page<UserOperationDTO>> getHistory(
            @Parameter(description = "Filter by operation type (ADD, SUBTRACT, MULTIPLY, DIVIDE, SQRT)")
            @RequestParam(required = false) Optional<String> operationType,
            @Parameter(description = "Filter by start date (ISO format)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> startDate,
            @Parameter(description = "Filter by end date (ISO format)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> endDate,
            @Parameter(description = "Pagination information") 
            @PageableDefault(size = 10) Pageable pageable,
            @Parameter(description = "JWT token with Bearer prefix", hidden = true)
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        log.info("Getting operation history");
        log.debug("operationType: {}, startDate: {}, endDate: {}, pageable: {}",
                operationType, startDate, endDate, pageable);

        String token = jwtTokenUtil.validateAuthorizationHeader(authorizationHeader);
        Page<UserOperationDTO> operations = operationHistoryService.getOperations(
                operationType, startDate, endDate, pageable, token);

        return ResponseEntity.ok(operations);
    }

    @Operation(
        summary = "Get operation by ID", 
        description = "Retrieves a specific calculation by its ID",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved operation", 
                    content = @Content(schema = @Schema(implementation = UserOperationDTO.class)))
    })
    @GetMapping(path = "{id}")
    public ResponseEntity<UserOperationDTO> getOperationById(
            @Parameter(description = "ID of the operation to retrieve") 
            @PathVariable Long id,
            @Parameter(description = "JWT token with Bearer prefix", hidden = true)
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        log.info("Getting operation");
        log.debug("Getting operation by ID: {}", id);

        String token = jwtTokenUtil.validateAuthorizationHeader(authorizationHeader);
        UserOperationDTO operation = operationHistoryService.getOperationById(id, token);

        return ResponseEntity.ok(operation);
    }

    @Operation(
        summary = "Delete operation by ID", 
        description = "Deletes a specific calculation by its ID",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Operation successfully deleted")
    })
    @DeleteMapping(path = "{id}")
    public ResponseEntity<UserOperationDTO> deleteOperationById(
            @Parameter(description = "ID of the operation to delete") 
            @PathVariable Long id,
            @Parameter(description = "JWT token with Bearer prefix", hidden = true)
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        log.info("Deleting operation");
        log.debug("Deleting operation by ID: {}", id);

        String token = jwtTokenUtil.validateAuthorizationHeader(authorizationHeader);
        operationHistoryService.deleteOperationById(id, token);

        return ResponseEntity.noContent().build();
    }
}
