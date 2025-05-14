package com.mx.raven.calculator.service.impl;

import com.mx.raven.calculator.model.AuthenticationRequest;
import com.mx.raven.calculator.model.AuthenticationResponse;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.persistence.repositories.UserEventRepository;
import com.mx.raven.calculator.security.JwtTokenUtil;
import com.mx.raven.calculator.validation.UserSaveValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserEventRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserSaveValidator userSaveValidator;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTO;
    private AuthenticationRequest authRequest;
    private String encodedPassword;
    private String token;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setEmail("test@example.com");

        authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        encodedPassword = "encodedPassword";
        token = "jwt-token";
    }

    @Test
    void registerUser_Success() {
        doNothing().when(userSaveValidator).validate(any(UserDTO.class));
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(repository.storeSaveUser(any(UserDTO.class))).thenReturn(userDTO);

        UserDTO result = userService.registerUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertNull(result.getPassword());

        verify(userSaveValidator).validate(any(UserDTO.class));
        verify(repository).storeSaveUser(any(UserDTO.class));
    }

    @Test
    void registerUser_ValidationFails() {
        doThrow(new IllegalArgumentException("Validation failed")).when(userSaveValidator).validate(any(UserDTO.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userDTO);
        });
        
        assertEquals("Validation failed", exception.getMessage());

        verify(userSaveValidator).validate(any(UserDTO.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).storeSaveUser(any(UserDTO.class));
    }

    @Test
    void authenticateUser_Success() {
        UserDTO foundUser = new UserDTO();
        foundUser.setId(1L);
        foundUser.setUsername("testuser");
        foundUser.setPassword(encodedPassword);
        
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        UserDetails userDetails = new User("testuser", "password", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn(token);

        AuthenticationResponse response = userService.authenticateUser(authRequest);

        assertNotNull(response);
        assertEquals(token, response.getToken());

        verify(repository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), foundUser.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void authenticateUser_UserNotFound() {
        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            userService.authenticateUser(authRequest);
        });
        
        assertEquals("Login failed. Incorrect username or password", exception.getMessage());

        verify(repository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateUser_IncorrectPassword() {
        UserDTO foundUser = new UserDTO();
        foundUser.setId(1L);
        foundUser.setUsername("testuser");
        foundUser.setPassword(encodedPassword);
        
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            userService.authenticateUser(authRequest);
        });
        
        assertEquals("Login failed. Incorrect username or password", exception.getMessage());

        verify(repository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), foundUser.getPassword());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}