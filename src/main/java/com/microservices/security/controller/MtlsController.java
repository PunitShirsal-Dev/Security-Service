package com.microservices.security.controller;

import com.microservices.security.service.MtlsClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service/mtls")
@RequiredArgsConstructor
public class MtlsController {

    private final MtlsClientService mtlsClientService;

    @GetMapping("/call")
    @PreAuthorize("hasAnyRole('SERVICE','ADMIN')")
    public ResponseEntity<String> call(@RequestParam String url) {
        return ResponseEntity.ok(mtlsClientService.callWithMtls(url));
    }
}

