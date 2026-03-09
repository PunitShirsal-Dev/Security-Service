package com.microservices.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.time.Duration;

@Service
public class MtlsClientService {

    @Value("${security.mtls.enabled:false}")
    private boolean mtlsEnabled;

    @Value("${security.mtls.key-store}")
    private Resource keyStore;

    @Value("${security.mtls.key-store-password}")
    private String keyStorePassword;

    @Value("${security.mtls.trust-store}")
    private Resource trustStore;

    @Value("${security.mtls.trust-store-password}")
    private String trustStorePassword;

    @Value("${security.mtls.protocol:TLS}")
    private String protocol;

    public String callWithMtls(String url) {
        if (!mtlsEnabled) {
            return "mTLS disabled by configuration";
        }
        try {
            SSLContext sslContext = buildSslContext();
            HttpClient client = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return "mTLS call status=" + response.statusCode();
        } catch (Exception ex) {
            return "mTLS call failed: " + ex.getMessage();
        }
    }

    private SSLContext buildSslContext() throws Exception {
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        try (InputStream inputStream = keyStore.getInputStream()) {
            clientStore.load(inputStream, keyStorePassword.toCharArray());
        }

        KeyStore trust = KeyStore.getInstance("PKCS12");
        try (InputStream inputStream = trustStore.getInputStream()) {
            trust.load(inputStream, trustStorePassword.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, keyStorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trust);

        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }
}

