package com.example.chook.file;

import com.example.chook.entity.UploadedFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
  private static final String DELETE_FAIL_LOG_FILE = "delete-failed-files.csv";

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
   * @throws IllegalStateException 파일 삭제 실패 기록에 실패해 DB 삭제가 진행되지 않았을 때 예외 발생
   */
  private void deleteFile(UploadedFile file) {
    Path absoluteUploadDir = Paths.get(this.uploadDir);
    Path targetPath = absoluteUploadDir
      .resolve(file.getRelativePath())
      .resolve(file.getStoredName());
    if (!deletePhysicalFile(targetPath) && !recordDeleteFailure(file)) {
      // 파일 삭제 기록도 실패 시 DB 삭제를 진행하지 않음
      throw new IllegalStateException("파일 삭제 실패 기록에 실패했습니다.");
    }
    uploadedFileRepository.delete(file);
  }

  /**
   * 물리 저장소에서 파일을 삭제한다.
   * @param path 삭제할 파일의 경로
   * @return 파일 삭제 성공 여부
   */
  private boolean deletePhysicalFile(Path path) {
    // 파일 자체가 존재하지 않는 것은 이미 파일 삭제가 완료된 것으로 취급
    // 파일 삭제에 실패한 경우는 lock 등으로 인해 삭제 권한이 막힌 경우 등
    try {
      Files.deleteIfExists(path);
      return true;
    } catch (IOException exception) {
      log.error("파일 \"{}\" 삭제 실패", path, exception);
      return false;
    }
  }


  private synchronized boolean recordDeleteFailure(UploadedFile file) {

    Path absoluteSystemDir = Paths.get(this.systemDir);
    Path deleteFailedLogPath = absoluteSystemDir.resolve(DELETE_FAIL_LOG_FILE);

    try {
      Files.createDirectories(Paths.get(systemDir));
      boolean newFile = Files.notExists(deleteFailedLogPath);
      try (
        BufferedWriter writer = Files.newBufferedWriter(
          deleteFailedLogPath,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
      ) {
        if (newFile)
          printer.printRecord("uuid_str", "relative_path", "stored_name");
        printer.printRecord(file.getUuid().toString(), file.getRelativePath(), file.getStoredName());
        return true;
      }
    } catch (IOException exception) {
      log.error("파일 삭제 실패 로그 작성 실패", exception);
      return false;
    }
  }
}
