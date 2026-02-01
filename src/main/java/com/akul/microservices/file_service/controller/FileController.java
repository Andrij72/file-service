package com.akul.microservices.file_service.controller;

import com.akul.microservices.file_service.dto.FileDto;
import com.akul.microservices.file_service.service.FileStorageService;
import com.akul.microservices.file_service.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

/**
 * REST controller for handling product image operations in MinIO.
 *
 * Supports upload, preview, and download.
 * Ensures correct objectName usage from Service layer (no manual path guessing).
 *
 * Author: Andrii Kulynych
 * Since: 1/27/2026
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final ProductImageService productImageService;
    private final FileStorageService fileStorageService;

    /**
     * Uploads a product image and returns FileDto with full objectName and presigned URL.
     *
     * @param file multipart file
     * @param sku  SKU of the product
     * @return FileDto with objectName, contentType, size, presigned URL
     */
    @PostMapping("/upload/product/{sku}")
    public ResponseEntity<FileDto> uploadProductImageBySku(
            @RequestParam("file") MultipartFile file,
            @PathVariable String sku
    ) {
        try {
            FileDto dto = productImageService.uploadProductImageBySku(file, sku);
            log.info("Product image uploaded, SKU={}, objectName={}", sku, dto.objectName());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Failed to upload product image, SKU={}", sku, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generates a presigned URL for previewing a product image.
     * Uses exact objectName returned from upload to ensure correct path.
     *
     * @param objectName full path of the object in MinIO (from FileDto.objectName)
     * @return presigned URL valid for 1 hour
     */
    @GetMapping("/preview")
    public ResponseEntity<String> previewFile(
            @RequestParam("objectName") String objectName
    ) {
        try {
            String url = fileStorageService.presignedUrl(objectName, Duration.ofHours(1));
            log.info("Preview URL generated for objectName={}", objectName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.warn("Failed to generate preview URL for objectName={}", objectName, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Downloads a product image via streaming.
     * Uses exact objectName from upload to ensure correct path.
     *
     * @param objectName full path of the object in MinIO (from FileDto.objectName)
     * @return streamed file
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam("objectName") String objectName
    ) {
        try {
            String filename = objectName.substring(objectName.lastIndexOf('/') + 1);
            InputStreamResource resource = fileStorageService.download(objectName);

            log.info("Downloading file, objectName={}", objectName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to download file for objectName={}", objectName, e);
            return ResponseEntity.notFound().build();
        }
    }
}
