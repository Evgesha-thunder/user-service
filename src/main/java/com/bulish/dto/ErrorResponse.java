package com.bulish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String title;
    private final String message;
    private final Instant timestamp;
    private final Map<String, String> fieldErrors;
}
