package com.microservices.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ApiKeyCreateResponse {
    private Long id;
    private String apiKey;
    private Instant expiresAt;
}

