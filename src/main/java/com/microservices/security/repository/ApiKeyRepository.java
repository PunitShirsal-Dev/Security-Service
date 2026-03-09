package com.microservices.security.repository;

import com.microservices.security.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {
    Optional<ApiKeyEntity> findByKeyHashAndActiveTrue(String keyHash);
}

