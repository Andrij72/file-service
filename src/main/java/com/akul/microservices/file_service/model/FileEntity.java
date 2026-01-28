package com.akul.microservices.file_service.model;

/**
 * FileEntity.java.
 *
 * @author Andrii Kulynych
 * @since 1/23/2026
 */
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objectName;
    private String contentType;
    private long size;
}
