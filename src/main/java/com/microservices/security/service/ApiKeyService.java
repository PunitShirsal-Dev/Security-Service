package com.microservices.security.service;

import com.microservices.security.dto.ApiKeyCreateRequest;
import com.microservices.security.dto.ApiKeyCreateResponse;
import com.microservices.security.dto.ApiKeyValidationResponse;
import com.microservices.security.entity.ApiKeyEntity;
import com.microservices.security.exception.ApiKeyNotFoundException;
import com.microservices.security.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    @Value("${security.api-key.default-validity-days:90}")
    private int defaultValidityDays;

    @Transactional
    public ApiKeyCreateResponse create(ApiKeyCreateRequest request) {
        String rawApiKey = generateApiKey();
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setName(request.getName());
        entity.setRoles(normalizeRoles(request.getRoles()));
        entity.setKeyHash(hash(rawApiKey));
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());
        int validityDays = request.getValidityDays() == null ? defaultValidityDays : Math.max(request.getValidityDays(), 1);
        entity.setExpiresAt(Instant.now().plus(validityDays, ChronoUnit.DAYS));
        ApiKeyEntity saved = apiKeyRepository.save(entity);
        return new ApiKeyCreateResponse(saved.getId(), rawApiKey, saved.getExpiresAt());
    }

    @Transactional(readOnly = true)
    public ApiKeyValidationResponse validate(String apiKey) {
        ApiKeyEntity entity = apiKeyRepository.findByKeyHashAndActiveTrue(hash(apiKey))
                .orElse(null);
        if (entity == null) {
            return new ApiKeyValidationResponse(false, "anonymous", "", "API key not found");
        }
        if (entity.getExpiresAt().isBefore(Instant.now())) {
            return new ApiKeyValidationResponse(false, entity.getName(), entity.getRoles(), "API key expired");
        }
        return new ApiKeyValidationResponse(true, entity.getName(), entity.getRoles(), "API key valid");
    }

    @Transactional
    public void revoke(Long id) {
        ApiKeyEntity entity = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ApiKeyNotFoundException("API key not found for id: " + id));
        entity.setActive(false);
        apiKeyRepository.save(entity);
    }

    private String normalizeRoles(String roles) {
        return roles.replace(" ", "").toUpperCase();
    }

    private String generateApiKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return new String(Hex.encode(hashBytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", ex);
        }
    }
}

