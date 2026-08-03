package com.example.chook.file.service;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.FileDeletionFailureRecorder;
import com.example.chook.file.FileStorage;
import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.record.FilePath;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileServiceImpl implements FileService {

  private final UploadedFileRepository uploadedFileRepository;
  private final FileStorage fileStorage;
  private final FileDeletionFailureRecorder failureRecorder;

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

  /**
   * UUID 문자열을 기준으로 파일을 삭제한다.
   *
   * @param uuidStr 삭제할 파일의 UUID 문자열
   */
  @Transactional
  @Override
  public void delete(String uuidStr) {
    Optional<UploadedFile> targetFile = uploadedFileRepository.findById(UUID.fromString(uuidStr));
    if (targetFile.isEmpty()) return;
    deleteFile(targetFile.get());
  }

  @Override
  public FileResource getFile(String uuidStr) {
    UploadedFile targetFile = uploadedFileRepository.findById(UUID.fromString(uuidStr))
      .orElseThrow(() -> new RuntimeException(String.format("UUID가 \"%s\"인 파일을 DB에서 찾을 수 없음", uuidStr)));

    Resource resource = fileStorage.getFile(
      targetFile.getRelativePath(),
      targetFile.getStoredName()
    );

    FileResource result = new FileResource(
      resource,
      targetFile.getMimeType(),
      targetFile.getOriginalName()
    );

    log.info("FileResource: {}", result);

    return result;
  }

  private UploadedFile upload(MultipartFile file, String relativePath) {

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

    if (!fileStorage.store(file, relativePath, storedFileName)) {
      throw new IllegalStateException(
        String.format("파일 \"%s\" 업로드에 실패했습니다.", originalFileName)
      );
    }

    try {
      return uploadedFileRepository.save(uploadedFile);
    } catch (RuntimeException exception) {
      log.error("파일 \"{}\" DB 저장 실패. 파일 삭제를 시도합니다.", originalFileName, exception);
      if (!fileStorage.delete(relativePath, storedFileName)) {
        throw new IllegalStateException(
          String.format("파일 \"%s\" 업로드 실패 후 파일 정리에도 실패", storedFileName),
          exception);
      }
      throw exception;
    }
  }

  /**
   * 파일을 물리 저장소와 DB에서 삭제한다.
   *
   * @param file 삭제할 파일 Entity
   * @throws IllegalStateException 파일 삭제 실패 기록에 실패해 DB 삭제가 진행되지 않았을 때 예외 발생
   */
  private void deleteFile(UploadedFile file) {
    if (!fileStorage.delete(
      file.getRelativePath(),
      file.getStoredName()
    )) {
      log.error("파일 \"{}\"에 대한 실패 기록 작성을 시도합니다.", file.getOriginalName());
      if (!failureRecorder.recordDeletionFailure(new FilePath(
        file.getRelativePath(),
        file.getStoredName()
      ))) {
        // 파일 삭제 기록도 실패 시 DB 삭제를 진행하지 않음
        throw new IllegalStateException("파일 삭제 실패 기록에 실패했습니다.");
      }
    }
    uploadedFileRepository.delete(file);
  }


}
