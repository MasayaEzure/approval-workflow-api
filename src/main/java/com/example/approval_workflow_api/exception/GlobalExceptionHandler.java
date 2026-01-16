package com.example.approval_workflow_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        String message = e.getMessage();
        
        // メッセージに基づいて適切なHTTPステータスを決定
        HttpStatus status;
        if (message != null && message.contains("見つかりません")) {
            status = HttpStatus.NOT_FOUND; // 404
        } else if (message != null && (message.contains("状態が不正") || message.contains("状態遷移"))) {
            status = HttpStatus.CONFLICT; // 409
        } else {
            status = HttpStatus.BAD_REQUEST; // 400
        }
        
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}

