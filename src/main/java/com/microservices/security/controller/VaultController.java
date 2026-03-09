package com.microservices.security.controller;

import com.microservices.security.service.VaultSecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/vault")
@RequiredArgsConstructor
public class VaultController {

    private final VaultSecretService vaultSecretService;

    @GetMapping("/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> read(@RequestParam(defaultValue = "secret/data/app") String path) {
        return ResponseEntity.ok(vaultSecretService.readSecret(path));
    }
}

