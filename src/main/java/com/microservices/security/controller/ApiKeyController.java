package com.microservices.security.controller;

import com.microservices.security.dto.ApiKeyCreateRequest;
import com.microservices.security.dto.ApiKeyCreateResponse;
import com.microservices.security.dto.ApiKeyValidationResponse;
import com.microservices.security.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apikey")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiKeyCreateResponse> create(@Valid @RequestBody ApiKeyCreateRequest request) {
        return ResponseEntity.ok(apiKeyService.create(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiKeyValidationResponse> validate(@RequestParam("key") String key) {
        return ResponseEntity.ok(apiKeyService.validate(key));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revoke(@PathVariable Long id) {
        apiKeyService.revoke(id);
        return ResponseEntity.noContent().build();
    }
}

