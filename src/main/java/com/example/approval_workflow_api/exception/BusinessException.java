package com.example.approval_workflow_api.exception;

public class BusinessException extends RuntimeException {
    private final BusinessErrorCode code;

    public BusinessException(BusinessErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessErrorCode getCode() {
        return code;
    }
}