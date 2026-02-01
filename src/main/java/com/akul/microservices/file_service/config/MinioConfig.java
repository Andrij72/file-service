package com.akul.microservices.file_service.config;

import com.akul.microservices.file_service.props.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * MinioConfig.java.
 *
 * @author Andrii Kulynych
 * @since 1/22/2026
 */
@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties properties;

    @Bean("internalMinioClient")
    @Primary
    public MinioClient internalMinioClient() {
        validateProperties(properties.getUrl(), properties.getAccessKey(), properties.getSecretKey());
        return MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    @Bean("publicMinioClient")
    public MinioClient publicMinioClient() {
        validateProperties(properties.getPublicUrl(), properties.getAccessKey(), properties.getSecretKey());
        return MinioClient.builder()
                .endpoint(properties.getPublicUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    private void validateProperties(String url, String accessKey, String secretKey) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("MinIO endpoint must not be null or empty");
        }
        if (accessKey == null || accessKey.isBlank()) {
            throw new IllegalArgumentException("MinIO accessKey must not be null or empty");
        }
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("MinIO secretKey must not be null or empty");
        }
    }
}
