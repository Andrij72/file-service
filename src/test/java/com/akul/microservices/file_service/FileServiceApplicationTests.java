package com.akul.microservices.file_service;

import com.akul.microservices.file_service.props.MinioProperties;
import com.akul.microservices.file_service.service.FileStorageService;
import io.minio.MinioClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FileServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileStorageService fileStorageService;


    @Autowired
    private MinioClient internalMinioClient;

    @Autowired
    private MinioClient publicMinioClient;

    @Autowired
    private MinioProperties minioProperties;


    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(internalMinioClient, publicMinioClient, minioProperties);
    }

    @LocalServerPort
    private Integer port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.3.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void mysqlProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @TestConfiguration
    static class MinioTestConfig {
        @Bean
        @Primary
        public MinioClient internalMinioClient() {
            return Mockito.mock(MinioClient.class);
        }

        @Bean
        public MinioClient publicMinioClient() {
            return Mockito.mock(MinioClient.class);
        }
    }

    @BeforeEach
    void setUp(@Autowired MinioClient internalMinioClient) throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        ReflectionTestUtils.setField(fileStorageService, "minioClient", internalMinioClient);

        Mockito.when(internalMinioClient.getPresignedObjectUrl(Mockito.any()))
                .thenReturn("http://localhost:9100/test.webp");
    }

    @Disabled
    @Test
    void contextLoads() {
    }

    @Disabled
    @Test
    void testPreviewEndpoint() throws Exception {
        String objectName = "products/SKU1/images/test.webp";

        mockMvc.perform(get("/api/v1/files/preview")
                        .param("objectName", objectName))
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost:9100/test.webp"));
    }
}
