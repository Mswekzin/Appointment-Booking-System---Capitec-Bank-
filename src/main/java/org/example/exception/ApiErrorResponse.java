package org.example.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(String message, LocalDateTime timestamp) {
}

