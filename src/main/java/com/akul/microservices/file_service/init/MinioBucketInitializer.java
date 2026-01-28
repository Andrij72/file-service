package com.akul.microservices.file_service.init;

import com.akul.microservices.file_service.props.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * MinioBucketInitializer.java.
 *
 * @author Andrii Kulynych
 * @since 1/22/2026
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioBucketInitializer {
    private final MinioClient minioClient;
    private final MinioProperties properties;

    @PostConstruct
    public void initBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucket())
                                .build()
                );
                log.info("Bucket '{}' created", properties.getBucket());
            } else
                log.info("Bucket '{}' already exists", properties.getBucket());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize MinIO bucket", e);
        }
    }
}
