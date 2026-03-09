package com.microservices.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class VaultSecretService {

    private final Optional<VaultTemplate> vaultTemplate;

    @Value("${vault.enabled:false}")
    private boolean vaultEnabled;

    public VaultSecretService(Optional<VaultTemplate> vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public Map<String, Object> readSecret(String path) {
        if (!vaultEnabled || vaultTemplate.isEmpty()) {
            return Collections.singletonMap("message", "Vault integration disabled");
        }
        try {
            return Optional.ofNullable(vaultTemplate.get().read(path))
                    .map(response -> response.getData())
                    .orElse(Collections.emptyMap());
        } catch (VaultException ex) {
            return Collections.singletonMap("error", "Vault read failed: " + ex.getMessage());
        }
    }
}

