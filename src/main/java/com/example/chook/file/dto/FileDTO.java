package com.example.chook.file.dto;

import com.example.chook.file.entity.enums.UploadedFileCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {

  private String uuid;
  private String name;
  private String saveDir;
  private Long size;
  private LocalDateTime uploadedAt;
  private String mimeType;
  private UploadedFileCategory category;

  public String getSizeStr() {
    if (size >= 1024 * 1024) return String.format("%.2f MB", (double) size / 1024 / 1024);
    if (size >= 1024) return String.format("%.2f KB", (double) size / 1024);
    return String.format("%d Bytes", size);
  }

  public boolean getIsImage() {
    return category == UploadedFileCategory.IMAGE;
  }

}
