package com.mx.raven.calculator.exceptions;

import com.mx.raven.calculator.model.dto.ApiErrorDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerException(Exception e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setBackendMessage(e.getLocalizedMessage());
        apiErrorDTO.setUrl(request.getRequestURL().toString());
        apiErrorDTO.setMethod(request.getMethod());
        apiErrorDTO.setMessage("Internal server error, please try again later.");
        apiErrorDTO.setTimestamp(LocalDateTime.now());

        log.error("Generic Error: ", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorDTO);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handlerException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Request method is not supported. ", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorDTO);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handlerException(NoResourceFoundException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("No existing service. ", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorDTO);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<?> handlerException(ObjectNotFoundException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Resource not found. ", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorDTO);
    }

    @ExceptionHandler(InvalidObjectException.class)
    public ResponseEntity<?> handlerException(InvalidObjectException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Invalid object exception. ", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorDTO);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handlerException(BadCredentialsException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Incorrect Username or password. ", e);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorDTO);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handlerException(UsernameNotFoundException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("User not found. ", e);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorDTO);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handlerException(MissingServletRequestParameterException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Required request parameter is not present. ", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorDTO);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handlerException(MissingRequestHeaderException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Required request header is not present. ", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorDTO);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handlerException(IllegalArgumentException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Validation error. ", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorDTO);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handlerException(DataIntegrityViolationException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        e = new DataIntegrityViolationException("Username or email already registered. Try again", e);
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Username or email already registered. Try again. ", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorDTO);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handlerException(SignatureException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Invalid token. Log in again and try again. ", e);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorDTO);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handlerException(ExpiredJwtException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        generalControlledExceptions(request, e, apiErrorDTO);

        log.error("Sesion expired. Log in again and try again. ", e);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorDTO);
    }

    private void generalControlledExceptions(HttpServletRequest request, Exception e, ApiErrorDTO apiErrorDTO) {
        apiErrorDTO.setBackendMessage(e.getCause() != null ? e.getCause().getLocalizedMessage() : e.getLocalizedMessage());
        apiErrorDTO.setUrl(request.getRequestURL().toString());
        apiErrorDTO.setMethod(request.getMethod());
        apiErrorDTO.setTimestamp(LocalDateTime.now());
        apiErrorDTO.setMessage(e.getMessage());
        apiErrorDTO.setTimestamp(LocalDateTime.now());
    }

}
