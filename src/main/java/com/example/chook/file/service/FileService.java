package com.example.chook.file.service;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.record.FileResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

  default FileDTO toDto(UploadedFile uploadedFile) {
    return FileDTO.builder()
      .uuid(uploadedFile.getUuid().toString())
      .name(uploadedFile.getOriginalName())
      .saveDir(uploadedFile.getRelativePath())
      .size(uploadedFile.getFileSize())
      .uploadedAt(uploadedFile.getUploadedAt())
      .mimeType(uploadedFile.getMimeType())
      .category(uploadedFile.getCategory())
      .build();
  }

  FileDTO uploadAndGetDto(MultipartFile file, String relativePath);

  List<FileDTO> getList();

  void delete(String uuidStr);

  FileResource getFile(String uuidStr);
}
