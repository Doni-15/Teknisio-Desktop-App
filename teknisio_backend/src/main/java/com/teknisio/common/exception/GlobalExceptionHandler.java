package com.teknisio.common.exception;

import com.teknisio.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException exception) {
    return ResponseEntity
      .status(exception.getStatus())
      .body(ApiResponse.error(exception.getMessage()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException exception) {
    HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());

    return ResponseEntity
      .status(status)
      .body(ApiResponse.error(exception.getReason()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
    MethodArgumentNotValidException exception
  ) {
    Map<String, String> errors = new LinkedHashMap<>();

    exception.getBindingResult().getFieldErrors().forEach(error ->
      errors.put(error.getField(), error.getDefaultMessage())
    );

    exception.getBindingResult().getGlobalErrors().forEach(error ->
      errors.put(error.getObjectName(), error.getDefaultMessage())
    );

    return ResponseEntity
      .badRequest()
      .body(ApiResponse.error("Validation failed", errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
    ConstraintViolationException exception
  ) {
    Map<String, String> errors = new LinkedHashMap<>();

    exception.getConstraintViolations().forEach(violation ->
      errors.put(violation.getPropertyPath().toString(), violation.getMessage())
    );

    return ResponseEntity
      .badRequest()
      .body(ApiResponse.error("Validation failed", errors));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
    HttpMessageNotReadableException exception
  ) {
    return ResponseEntity
      .badRequest()
      .body(ApiResponse.error("Invalid request body"));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
    DataIntegrityViolationException exception
  ) {
    log.warn("Database constraint violation", exception);

    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(ApiResponse.error("Data violates database constraint"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
    log.error("Unhandled exception", exception);

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.error("Internal server error"));
  }
}
