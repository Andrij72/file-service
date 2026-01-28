package com.akul.microservices.file_service.dto;

public record FileDto(
        String objectName,
        String contentType,
        long size,
        String presignedUrl
) {}
