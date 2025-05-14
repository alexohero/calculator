package com.mx.raven.calculator.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetails userDetails;
    private String secret;
    private long expiration;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        secret = "testSecretKey123456789012345678901234567890";
        expiration = 3600000;
        
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", expiration);
        
        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void generateToken_Success() {
        String token = jwtTokenUtil.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_Success() {
        String token = jwtTokenUtil.generateToken(userDetails);

        String username = jwtTokenUtil.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtTokenUtil.generateToken(userDetails);

        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 1L);
        String token = jwtTokenUtil.generateToken(userDetails);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThrows(ExpiredJwtException.class, () -> {
            jwtTokenUtil.validateToken(token, userDetails);
        });
    }

    @Test
    void validateToken_InvalidUsername_ReturnsFalse() {
        String token = jwtTokenUtil.generateToken(userDetails);
        UserDetails differentUser = new User("differentuser", "password", new ArrayList<>());

        boolean isValid = jwtTokenUtil.validateToken(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void validateAuthorizationHeader_ValidHeader_ReturnsToken() {
        String token = jwtTokenUtil.generateToken(userDetails);
        String authHeader = "Bearer " + token;

        String extractedToken = jwtTokenUtil.validateAuthorizationHeader(authHeader);

        assertEquals(token, extractedToken);
    }

    @Test
    void validateAuthorizationHeader_InvalidHeader_ThrowsException() {
        String invalidHeader = "Invalid " + jwtTokenUtil.generateToken(userDetails);

        Exception exception = assertThrows(SignatureException.class, () -> {
            jwtTokenUtil.validateAuthorizationHeader(invalidHeader);
        });
        
        assertEquals("Invalid Authorization header format", exception.getMessage());
    }

    @Test
    void validateAuthorizationHeader_NullHeader_ThrowsException() {
        Exception exception = assertThrows(SignatureException.class, () -> {
            jwtTokenUtil.validateAuthorizationHeader(null);
        });
        
        assertEquals("Invalid Authorization header format", exception.getMessage());
    }
}