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
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileServiceImpl implements FileService {

  private final UploadedFileRepository uploadedFileRepository;
  @Value("${file.upload-dir}")
  private String uploadDir;
  @Value("${file.system-dir}")
  private String systemDir;

  @Transactional
  @Override
  public FileDTO uploadAndGetDto(MultipartFile file, String relativePath) {
    return toDto(upload(file, relativePath));
  }

  @Override
  public List<FileDTO> getList() {
    return uploadedFileRepository.findAll().stream()
      .map(this::toDto).toList();
  }

  public UploadedFile upload(MultipartFile file, String relativePath) {

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
      .relativePath(relativePath)
      .mimeType(file.getContentType())
      .fileSize(file.getSize())
      .uploadedAt(LocalDateTime.now())
      .build();

    Path absoluteUploadDir = Paths.get(this.uploadDir).resolve(relativePath);
    Path targetPath = absoluteUploadDir.resolve(storedFileName);

    try {
      Files.createDirectories(absoluteUploadDir);
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
  /**
   * UUID 문자열을 기준으로 파일을 삭제한다.
   * @param uuidStr 삭제할 파일의 UUID 문자열
   */
  @Transactional
  @Override
  public void delete(String uuidStr) {
    UploadedFile targetFile = uploadedFileRepository.findByUuid(UUID.fromString(uuidStr));
    if (targetFile == null) return;
    deleteFile(targetFile);
  }

  /**
   * 파일을 물리 저장소와 DB에서 삭제한다.
   * @param file 삭제할 파일 Entity
   */
  private void deleteFile(UploadedFile file) {
    Path absoluteUploadDir = Paths.get(this.uploadDir);
    Path targetPath = absoluteUploadDir
      .resolve(file.getRelativePath())
      .resolve(file.getStoredName());
    if (!deletePhysicalFile(targetPath)) {
      recordDeleteFailure(file);
    }
    uploadedFileRepository.delete(file);
  }

  /**
   * 물리 저장소에서 파일을 삭제한다.
   * @param path 삭제할 파일의 경로
   * @return 파일 삭제 성공 여부
   */
  private boolean deletePhysicalFile(Path path) {
    try {
      Files.deleteIfExists(path);
      return true;
    } catch (IOException exception) {
      log.error("파일 \"{}\" 삭제 실패", path, exception);
      return false;
      // 물리적 파일 삭제 실패 시에도 DB 삭제는 진행하기 위해 Exception을 전파하지 않음
      // 추후 OrphanFileSweeper 등으로 주기적으로 다시 삭제하도록 구현하고자 함
    }
  }

  private void recordDeleteFailure(UploadedFile file) {

  }
}
