package com.example.chook.file.service;

import com.example.chook.file.FileProperties;
import com.example.chook.file.FileStorage;
import com.example.chook.file.record.FilePath;
import com.example.chook.file.record.UnreferencedFile;
import com.example.chook.file.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class FileSweeper {

  private final FileProperties properties;
  private final UploadedFileRepository uploadedFileRepository;
  private final FileService fileService;
  private final FileStorage fileStorage;

  @Scheduled(cron = "${file.sweep.unreferenced.cron}")
  public synchronized void sweepUnreferencedFiles() {

    log.info("미참조 파일 정리 시작");
    LocalDateTime now = LocalDateTime.now();
    Duration gracePeriod = properties.getSweep().getUnreferenced().getGracePeriod();

    List<UnreferencedFile> unreferencedFiles = uploadedFileRepository.findUnreferencedFiles();
    for (UnreferencedFile file : unreferencedFiles) {
      log.debug("\"{}\" 파일이 참조되지 않음", file.uuid());
      LocalDateTime expirationTime = file.uploadedAt().plus(gracePeriod);
      if (now.isAfter(expirationTime)) {
        try {
          fileService.delete(file.uuid());
          log.info("\"{}\" 미참조 파일 삭제 완료", file.uuid());
        } catch (IllegalStateException e) {
          log.error("\"{}\" 미참조 파일 삭제 실패", file.uuid(), e);
        }
      } else {
        log.debug("\"{}\" 미참조 파일을 삭제하지 않음 (유예 기간 [{}]이 지나지 않음)",
          file.uuid(),
          gracePeriod
        );
      }
    }
    log.info("미참조 파일 정리 종료");
  }


  @Scheduled(cron = "${file.sweep.untracked.cron}")
  public synchronized void sweepUntrackedFiles() {

    log.info("미추적 파일 정리 시작");

    Set<FilePath> filePathRecords = uploadedFileRepository.findAllFilePaths();

    Path uploadDir = Paths.get(properties.getUploadDir());
    Instant now = Instant.now();

    if (Files.notExists(uploadDir)) {
      log.info("업로드 경로 ({})가 존재하지 않아 미추적 파일 정리 종료", uploadDir);
      return;
    }

    try (Stream<Path> paths = Files.walk(uploadDir).filter(Files::isRegularFile)) {
      paths.forEach(filePath -> {
        String relativePath = uploadDir.relativize(filePath.getParent())
          .toString().replace('\\', '/');
        String fileName = filePath.getFileName().toString();
        if (!filePathRecords.contains(new FilePath(fileName, relativePath))) {
          try {
            log.debug("\"{}\" 파일이 DB에 존재하지 않음",
              uploadDir.relativize(filePath)
            );
            BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            Instant creationTime = attributes.creationTime().toInstant();
            Duration gracePeriod = properties.getSweep().getUntracked().getGracePeriod();
            Instant expirationTime = creationTime.plus(gracePeriod);

            // DB에 등록되지 않은 파일 중 유예 기간이 지난 것만 삭제
            if (now.isAfter(expirationTime)) {
              if (!fileStorage.delete(relativePath, fileName)) {
                log.error("\"{}\" 파일 삭제 실패", filePath);
              } else {
                log.info("\"{}\" 파일 삭제 완료", filePath);
              }
            } else {
              log.debug("\"{}\" 파일을 삭제하지 않음 (유예 기간 [{}]이 지나지 않음)",
                filePath,
                gracePeriod
              );
            }
          } catch (IOException e) {
            log.error("\"{}\" 파일 처리 중 오류 발생", filePath, e);
          }
        }
      });
    } catch (IOException e) {
      log.error("미추적 파일 정리 중 오류 발생", e);
    }

    log.info("미추적 파일 정리 종료");

  }

}
