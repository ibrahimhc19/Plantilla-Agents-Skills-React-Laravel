package com.example.demo.shared.exception;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record ApiError(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    String path) {
}
