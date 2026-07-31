package com.example.chook.file;

import com.example.chook.entity.UploadedFile;
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
      .category(uploadedFile.getCategory())
      .build();
  }

  FileDTO uploadAndGetDto(MultipartFile file, String relativePath);

  List<FileDTO> getList();

  void delete(String uuidStr);

}
