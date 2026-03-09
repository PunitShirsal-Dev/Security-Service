package com.microservices.security.controller;

import com.microservices.security.dto.SecureMessageResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RbacController {

    @GetMapping("/user/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<SecureMessageResponse> userEndpoint(Authentication authentication) {
        return ResponseEntity.ok(new SecureMessageResponse("User endpoint access granted", authentication.getName()));
    }

    @GetMapping("/admin/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SecureMessageResponse> adminEndpoint(Authentication authentication) {
        return ResponseEntity.ok(new SecureMessageResponse("Admin endpoint access granted", authentication.getName()));
    }

    @GetMapping("/service/internal")
    @PreAuthorize("hasAnyRole('SERVICE','ADMIN')")
    public ResponseEntity<SecureMessageResponse> serviceEndpoint(Authentication authentication) {
        return ResponseEntity.ok(new SecureMessageResponse("Service-to-service endpoint access granted", authentication.getName()));
    }
}

