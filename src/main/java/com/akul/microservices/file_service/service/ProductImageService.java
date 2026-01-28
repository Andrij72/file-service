package com.akul.microservices.file_service.service;

import com.akul.microservices.file_service.dto.FileDto;
import com.akul.microservices.file_service.util.ObjectNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Set;

/**
 * ProductImageService.java.
 *
 * @author Andrii Kulynych
 * @since 1/23/2026
 */
@Service
@RequiredArgsConstructor
public class ProductImageService {


    private final FileStorageService fileStorageService;
    private final ObjectNameGenerator objectNameGenerator;

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public FileDto uploadProductImageBySku(
            MultipartFile file,
            String sku
    ) throws Exception {

        validate(file);

        String objectName = objectNameGenerator.productImage(
                sku,
                file.getContentType()
        );

        fileStorageService.upload(file, objectName);

        String url = fileStorageService.presignedUrl(
                objectName,
                Duration.ofHours(1)
        );

        return new FileDto(
                objectName,
                file.getContentType(),
                file.getSize(),
                url
        );
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File too large");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }
}
