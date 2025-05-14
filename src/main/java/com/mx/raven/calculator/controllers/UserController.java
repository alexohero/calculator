package com.mx.raven.calculator.controllers;

import com.mx.raven.calculator.model.AuthenticationRequest;
import com.mx.raven.calculator.model.AuthenticationResponse;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API for user registration and login")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered", 
                    content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @PostMapping(path = "register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        log.info("Received registration request for user.");
        log.debug("User to register: {}", userDTO);
        UserDTO registeredUser = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful", 
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class)))
    })
    @PostMapping(path = "login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        log.info("Received login request for user");
        log.debug("Authentication request: {}", authenticationRequest);
        AuthenticationResponse authenticationResponse = userService.authenticateUser(authenticationRequest);
        return ResponseEntity.ok(authenticationResponse);
    }
}
