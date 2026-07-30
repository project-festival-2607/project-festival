package com.example.chook.file;

import com.example.chook.entity.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  UploadedFile upload(MultipartFile file);

  void delete(UploadedFile file);

}
