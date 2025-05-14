package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.model.AuthenticationRequest;
import com.mx.raven.calculator.model.AuthenticationResponse;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.persistence.repositories.UserEventRepository;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.service.UserService;
import com.mx.raven.calculator.validation.UserSaveValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserEventRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserSaveValidator userSaveValidator;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        log.info("Registering user");
        log.debug("User to register: {}", userDTO);

        userSaveValidator.validate(userDTO);
        log.debug("Valid user");

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userDTO.setCreatedAt(LocalDateTime.now());

        var userDtoSaved = repository.storeSaveUser(userDTO);

        //Se setea null solo en el response por seguridad
        userDtoSaved.setPassword(null);
        log.debug("User DTO saved: {}", userDtoSaved);
        log.info("User registered successfully");

        return userDtoSaved;
    }

    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) {
        log.info("Authenticating user: {}", authenticationRequest.getUsername());

        var user = repository.findByUsername(authenticationRequest.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Login failed. Incorrect username or password"));

        log.debug("User found: {}", user.getUsername());

        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            log.debug("Invalid password for user: {}", authenticationRequest.getUsername());
            throw new BadCredentialsException("Login failed. Incorrect username or password");
        }

        log.info("Credentials validated successfully");

        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()
            )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        return new AuthenticationResponse(token);
    }
}
