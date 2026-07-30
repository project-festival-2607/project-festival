package com.example.chook.file;

import com.example.chook.entity.UploadedFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileServiceImpl implements FileService {

  private final UploadedFileRepository uploadedFileRepository;
  @Value("${file.upload-dir}")
  private String baseDir;

  @Transactional
  @Override
  public UploadedFile upload(MultipartFile file) {

    UUID uuid = UUID.randomUUID();
    String originalFileName = file.getOriginalFilename();
    String extension = FilenameUtils.getExtension(originalFileName);
    String storedFileName = (extension == null || extension.isBlank())
      ? uuid.toString()
      : String.format("%s.%s", uuid, extension);

    UploadedFile uploadedFile = UploadedFile.builder()
      .uuid(uuid)
      .originalName(originalFileName)
      .storedName(storedFileName)
      .saveDir(baseDir)
      .mimeType(file.getContentType())
      .fileSize(file.getSize())
      .uploadedAt(LocalDateTime.now())
      .build();

    Path uploadDir = Paths.get(baseDir);
    Path targetPath = uploadDir.resolve(storedFileName);

    try {
      Files.createDirectories(uploadDir);
      file.transferTo(targetPath);
    } catch (IOException exception) {
      log.error("파일 \"{}\" 업로드 실패", originalFileName, exception);
      deletePhysicalFile(targetPath);
      throw new IllegalStateException(
        String.format("파일 \"%s\" 업로드에 실패했습니다.", originalFileName),
        exception
      );
    }
    try {
      return uploadedFileRepository.save(uploadedFile);
    } catch (RuntimeException exception) {
      log.error("파일 \"{}\" DB 저장 실패", originalFileName, exception);
      deletePhysicalFile(targetPath);
      throw exception;
    }
  }

  @Transactional
  @Override
  public void delete(UploadedFile file) {
    Path targetPath = Paths.get(file.getSaveDir()).resolve(file.getStoredName());
    deletePhysicalFile(targetPath);
    uploadedFileRepository.delete(file);
  }

  private void deletePhysicalFile(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException exception) {
      log.error("파일 \"{}\" 삭제 실패", path, exception);
      // 물리적 파일 삭제 실패 시에도 DB 삭제는 진행하기 위해 Exception을 전파하지 않음
      // 추후 OrphanFileSweeper 등으로 주기적으로 다시 삭제하도록 구현하고자 함
    }
  }
}
