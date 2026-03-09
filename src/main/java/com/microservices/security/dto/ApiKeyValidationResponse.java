package com.microservices.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiKeyValidationResponse {
    private boolean valid;
    private String principal;
    private String roles;
    private String message;
}

