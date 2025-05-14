package com.mx.raven.calculator.controllers;

import com.mx.raven.calculator.model.dto.UserOperationDTO;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.service.UserOperationService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "calculate")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calculator", description = "API for performing calculator operations")
public class UserOperationController {

    private final UserOperationService userOperationService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(
        summary = "Perform a calculation", 
        description = "Performs a calculation with the specified operation and operands",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculation successful", 
                    content = @Content(schema = @Schema(implementation = UserOperationDTO.class)))
    })
    @PostMapping
    public ResponseEntity<UserOperationDTO> calculate(
            @Parameter(description = "Operation to perform (ADD, SUBTRACT, MULTIPLY, DIVIDE, SQRT)")
            @RequestParam String operation,
            @Parameter(description = "First operand") 
            @RequestParam BigDecimal operandA,
            @Parameter(description = "Second operand") 
            @RequestParam BigDecimal operandB,
            @Parameter(description = "JWT token with Bearer prefix", hidden = true)
            @RequestHeader(value = "Authorization") String authorizationHeader) {
        log.info("Received calculation request");
        log.debug("Operation: {}, operandA: {}, operandB: {}", operation, operandA, operandB);

        String token = jwtTokenUtil.validateAuthorizationHeader(authorizationHeader);
        var result = userOperationService.calculate(operation, operandA, operandB, token);

        return ResponseEntity.ok(result);
    }
}
