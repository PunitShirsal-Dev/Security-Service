package com.microservices.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecureMessageResponse {
    private String message;
    private String principal;
}

