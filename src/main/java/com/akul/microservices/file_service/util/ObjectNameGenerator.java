package com.akul.microservices.file_service.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ObjectNameGenerator.java.
 *
 * @author Andrii Kulynych
 * @since 1/23/2026
 */
@Component
public class ObjectNameGenerator {

    public String productImage(String sku, String contentType) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("productId must not be null or blank");
        }

        String extension = extension(contentType);

        return "products/%s/images/%s.%s"
                .formatted(sku, UUID.randomUUID(), extension);
    }

    private String extension(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content-Type is missing");
        }

        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png"  -> "png";
            case "image/webp" -> "webp";
            default -> throw new IllegalArgumentException(
                    "Unsupported content type: " + contentType
            );
        };
    }
}
