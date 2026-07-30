package com.example.chook.file;

import com.example.chook.entity.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  default FileDTO toDto(UploadedFile uploadedFile) {
    return FileDTO.builder()
      .uuid(uploadedFile.getUuid().toString())
      .originalName(uploadedFile.getOriginalName())
      .saveDir(uploadedFile.getSaveDir())
      .fileSize(uploadedFile.getFileSize())
      .uploadedAt(uploadedFile.getUploadedAt())
      .build();
  }

  FileDTO uploadAndGetDto(MultipartFile file);

  void delete(UploadedFile file);

}
