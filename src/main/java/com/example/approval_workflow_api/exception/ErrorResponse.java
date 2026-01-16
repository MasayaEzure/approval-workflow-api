package com.example.approval_workflow_api.exception;

import java.time.Instant;

public record ErrorResponse(
    String code,
    String message,
    Instant timestamp,
    String path
) {}