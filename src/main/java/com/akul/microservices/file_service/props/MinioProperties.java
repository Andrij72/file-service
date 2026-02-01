package com.akul.microservices.file_service.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ConfigFileService.java.
 *
 * @author Andrii Kulynych
 * @since 1/21/2026
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private boolean enabled;
    private String url;
    private String publicUrl;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
