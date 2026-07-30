package com.example.chook.entity;

import com.example.chook.entity.enums.UploadedFileCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "uploaded_file")
public class UploadedFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "file_id")
  private Long id;

  @JdbcTypeCode(SqlTypes.BINARY)  // Hibernate 구현체에 Java 타입을 어떤 JDBC 타입으로 매핑할지 명시
  @Column(name = "file_uuid", columnDefinition = "BINARY(16)", nullable = false, unique = true)
  private UUID uuid;

  @Column(name = "file_original_name", nullable = false, length = 255)
  private String originalName;

  @Column(name = "file_save_dir", nullable = false, length = 512)
  private String saveDir;

  @Column(name = "file_category", nullable = false)
  @Enumerated(EnumType.STRING)
  private UploadedFileCategory category;

  @Column(name = "file_mime_type", nullable = false, length = 255)
  private String mimeType;

  @Column(name = "file_size", nullable = false)
  private Long fileSize;

  @Column(name = "file_uploaded_at", nullable = false)
  private LocalDateTime uploadedAt;


}
