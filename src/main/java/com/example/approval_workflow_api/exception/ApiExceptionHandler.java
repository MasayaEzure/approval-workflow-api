package com.example.approval_workflow_api.exception;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
        BusinessException ex,
        HttpServletRequest request
    ) {
        HttpStatus status = switch (ex.getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;      // 404
            case INVALID_STATE -> HttpStatus.CONFLICT;   // 409
            case FORBIDDEN -> HttpStatus.FORBIDDEN;      // 403
        };

        ErrorResponse body = new ErrorResponse(
            ex.getCode().name(),
            ex.getMessage(),
            Instant.now(),
            request.getRequestURI()
        );

        return ResponseEntity.status(status).body(body);
    }

    // DTOバリデーションエラー（@Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .orElse("validation error");

        ErrorResponse body = new ErrorResponse(
            "VALIDATION_ERROR",
            msg,
            Instant.now(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // パラメータバリデーション（@Validated）
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException ex,
        HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
            "VALIDATION_ERROR",
            ex.getMessage(),
            Instant.now(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 最後の砦（想定外）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(
        Exception ex,
        HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
            "INTERNAL_ERROR",
            "Unexpected error occurred",
            Instant.now(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}