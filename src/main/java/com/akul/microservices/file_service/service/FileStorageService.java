package com.akul.microservices.file_service.service;

import com.akul.microservices.file_service.props.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;

@Service
public class FileStorageService {

    private final MinioClient internalMinioClient;
    private final MinioClient publicMinioClient;
    private final MinioProperties props;

    public FileStorageService(
            @Qualifier("internalMinioClient") MinioClient internalMinioClient,
            @Qualifier("publicMinioClient") MinioClient publicMinioClient,
            MinioProperties props
    ) {
        this.internalMinioClient = internalMinioClient;
        this.publicMinioClient = publicMinioClient;
        this.props = props;
    }

    public void upload(MultipartFile file, String objectName) throws Exception {
        try (InputStream is = file.getInputStream()) {
            internalMinioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(props.getBucket())
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
    }

    public InputStreamResource download(String objectName) throws Exception {
        InputStream is = internalMinioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(props.getBucket())
                        .object(objectName)
                        .build()
        );
        return new InputStreamResource(is);
    }

    public String presignedUrl(String objectName, Duration expiry) throws Exception {
        return publicMinioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(props.getBucket())
                        .object(objectName)
                        .expiry((int) expiry.getSeconds())
                        .build()
        );
    }
}
