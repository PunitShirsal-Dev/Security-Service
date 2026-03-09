package com.microservices.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiKeyCreateRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    private String roles;

    private Integer validityDays;
}

