package com.example.chook.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileStorage {

  private final FileProperties properties;

  public boolean store(MultipartFile file, String relativePath, String storedFileName) {

    Path absoluteTargetDir = Paths.get(properties.getUploadDir()).resolve(relativePath);
    Path targetPath = absoluteTargetDir.resolve(storedFileName);

    try {
      Files.createDirectories(absoluteTargetDir);
    } catch (IOException exception) {
      log.error("파일 저장 경로 \"{}\" 생성 실패", relativePath, exception);
      return false;
    }

    try {
      file.transferTo(targetPath);
      return true;
    } catch (IOException | IllegalStateException exception) {
      log.error("파일 \"{}\" 업로드 실패", storedFileName, exception);
      if (!delete(relativePath, storedFileName)) {
        throw new IllegalStateException(
          String.format("파일 \"%s\" 업로드 실패 후 파일 정리에도 실패", storedFileName),
          exception);
      }
      return false;
    }
  }

  public Resource getFile(String relativePath, String storedName) {

    Path absoluteUploadDir = Paths.get(properties.getUploadDir());
    Path targetPath = absoluteUploadDir
      .resolve(relativePath)
      .resolve(storedName);

    Resource resource = new FileSystemResource(targetPath);
    if (!resource.exists())
      throw new RuntimeException(String.format("\"%s\" 파일을 저장소에서 찾을 수 없음", targetPath));

    return resource;

  }

  public boolean delete(String relativePath, String storedFileName) {

    // 파일 자체가 존재하지 않는 것은 이미 파일 삭제가 완료된 것으로 취급
    // 파일 삭제에 실패한 경우는 lock 등으로 인해 삭제 권한이 막힌 경우 등

    Path absoluteTargetDir = Paths.get(properties.getUploadDir()).resolve(relativePath);
    Path targetPath = absoluteTargetDir.resolve(storedFileName);

    try {
      Files.deleteIfExists(targetPath);
      return true;
    } catch (IOException exception) {
      log.error("파일 \"{}\" 삭제 실패", storedFileName, exception);
      return false;
    }
  }
}
